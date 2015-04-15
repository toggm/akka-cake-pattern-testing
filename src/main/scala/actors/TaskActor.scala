package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import models.Task
import scala.concurrent.ExecutionContext.Implicits.global
import repositories.InMemoryTaskRepositoryImpl
import repositories.TaskRepository

object TaskActor {

  case object DeleteTasks
  case class AddTask(task: Task)
  case object GetTasks
  case object Ack

  def props(userId: String) = Props(classOf[DefaultTaskActor], userId)
}

trait TaskActorComponent {
  val taskRepository: TaskRepository
}

trait DefaultTaskActorComponent extends TaskActorComponent {
  val taskRepository: TaskRepository = new InMemoryTaskRepositoryImpl
}

class DefaultTaskActor(userId: String) extends TaskActor(userId) with DefaultTaskActorComponent

class TaskActor(userId: String) extends Actor with ActorLogging {
  self: TaskActorComponent =>
  import actors.TaskActor._

  def receive: Receive = {
    case DeleteTasks =>
      taskRepository.deleteByUser(userId)
      sender ! Ack
    case AddTask(task) =>
      taskRepository.insert(userId, task)
      sender ! Ack
    case GetTasks =>
      //store original sender locally to notify this sender when future terminates
      val origSender = sender
      taskRepository.getTasksByUser(userId).map { tasks =>
        origSender ! tasks
      }
  }
}