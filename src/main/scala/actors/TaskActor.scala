package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import repositories.TaskRepositoryComponent
import models.Task
import scala.concurrent.ExecutionContext.Implicits.global
import repositories.InMemoryTaskRepositoryImpl
import repositories.DefaultTaskRepositoryComponent

object TaskActor {

  case object DeleteTasks
  case class AddTask(task: Task)
  case object GetTasks
  case object Ack

  def props(userId: String) = Props(classOf[DefaultTaskActor], userId)
}

class DefaultTaskActor(userId: String) extends TaskActor(userId) with DefaultTaskRepositoryComponent

class TaskActor(userId: String) extends Actor with ActorLogging {
  self: TaskRepositoryComponent =>
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