package actors

import akka.actor._
import models._
import scala.concurrent.ExecutionContext.Implicits.global
import repositories._
import scala.concurrent.Future

object TaskActor {

  case object DeleteTasks
  case class AddTask(task: Task)
  case object GetTasks
  case object Ack

  def props(userId: String) = Props(classOf[DefaultTaskActor], userId)

  implicit class ExtendedActorRef(self: ActorRef) {
    def ->(f: => Future[_]) = {
      f.map(self ! _)
    }
  }
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
      //use extended function of actorref to store current sender reference
      sender -> taskRepository.getTasksByUser(userId)
  }
}