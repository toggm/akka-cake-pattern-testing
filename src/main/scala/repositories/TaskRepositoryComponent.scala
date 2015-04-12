package repositories

trait TaskRepositoryComponent {
  val taskRepository: TaskRepository
}

trait DefaultTaskRepositoryComponent extends TaskRepositoryComponent {
  val taskRepository: TaskRepository = new InMemoryTaskRepositoryImpl
}