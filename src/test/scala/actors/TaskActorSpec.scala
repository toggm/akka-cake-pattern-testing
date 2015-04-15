package actors

import org.specs2.mutable.Specification
import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.specs2.matcher.Scope
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ argThat, anyInt, eq => isEq }
import akka.testkit.TestProbe
import akka.actor.Props
import TaskActor._
import models.Task
import scala.concurrent.Future
import repositories.TaskRepository

class ActorTestScope extends TestKit(ActorSystem("test")) with Scope

class TaskActorSpec extends Specification with Mockito {
  "TaskActor insert" should {
    "invoke insert on repository" in new ActorTestScope {
      val userId = "noob"
      val probe = TestProbe()
      val taskRepository = mock[TaskRepository]
      val actorRef = system.actorOf(TaskActorMock.props(userId, taskRepository))
      val task = Task("task")

      probe.send(actorRef, AddTask(task))
      probe.expectMsg(Ack)

      there was one(taskRepository).insert(isEq(userId), isEq(task))
    }
  }

  "TaskActor delete" should {
    "invoke delete on repository" in new ActorTestScope {
      val userId = "noob"
      val probe = TestProbe()
      val taskRepository = mock[TaskRepository]
      val actorRef = system.actorOf(TaskActorMock.props(userId, taskRepository))

      probe.send(actorRef, DeleteTasks)
      probe.expectMsg(Ack)

      there was one(taskRepository).deleteByUser(isEq(userId))
    }
  }

  "TaskActor getByUser" should {
    "return tasks from repository" in new ActorTestScope {
      val userId = "noob"
      val probe = TestProbe()
      val taskActorComponent = new TaskActorComponentMock
      val actorRef = system.actorOf(TaskActorMock.props(userId, taskActorComponent))
      val tasks = Seq(Task("task1"), Task("task2"))

      taskActorComponent.taskRepository.getTasksByUser(isEq(userId)) returns Future.successful(tasks)

      probe.send(actorRef, GetTasks)
      probe.expectMsg(tasks)
    }
  }

}

/**
 * Use either component mock or create mocks in tastcase yourself
 */
class TaskActorComponentMock extends TaskActorComponent with Mockito {
  val taskRepository = mock[TaskRepository]
}

object TaskActorMock {
  def props(userId: String, taskRepository: TaskRepository) = Props(classOf[TaskActorMock], userId, taskRepository)
  def props(userId: String, taskActorComponent: TaskActorComponent) = Props(classOf[TaskActorMock], userId, taskActorComponent.taskRepository)
}

class TaskActorMock(userId: String, val taskRepository: TaskRepository) extends TaskActor(userId) with TaskActorComponent {
}