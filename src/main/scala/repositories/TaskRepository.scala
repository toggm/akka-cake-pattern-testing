package repositories

import scala.concurrent.Future
import models.Task

trait TaskRepository {
  def deleteByUser(userId: String): Future[Boolean]

  def insert(userId: String, task: Task): Future[Task]

  def getTasksByUser(userId: String): Future[Traversable[Task]]
}

class InMemoryTaskRepositoryImpl extends TaskRepository {

  var cache: Map[String, Seq[Task]] = Map()

  def deleteByUser(userId: String): Future[Boolean] = {
    cache = cache.filter(_._1 != userId)
    Future.successful(true)
  }

  def insert(userId: String, task: Task): Future[Task] = {
    val tasks = cache.get(userId).getOrElse(Seq())
    cache += (userId -> (tasks :+ task))
    Future.successful(task)
  }

  def getTasksByUser(userId: String): Future[Traversable[Task]] = {
    Future.successful(cache.get(userId).getOrElse(Seq()))
  }
}