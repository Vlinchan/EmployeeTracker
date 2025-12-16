package com.example.employeetracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.employeetracker.data.ActivityDao
import com.example.employeetracker.data.AppDatabase
import com.example.employeetracker.data.EmployeeDao
import com.example.employeetracker.data.TaskDao
import com.example.employeetracker.models.Activity
import com.example.employeetracker.models.ActivityType
import com.example.employeetracker.models.Employee
import com.example.employeetracker.models.Task
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        employeeDaoProvider: Provider<EmployeeDao>,
        taskDaoProvider: Provider<TaskDao>,
        activityDaoProvider: Provider<ActivityDao> // ✅ NEW
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "employee_tracker_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val employeeDao = employeeDaoProvider.get()
                    val taskDao = taskDaoProvider.get()
                    val activityDao = activityDaoProvider.get() // ✅ NEW

                    // Insert sample employees
                    employeeDao.insertEmployee(Employee(name = "Test Employee", email = "test@example.com", designation = "Tester", department = "QA", phoneNumber = "555-555-5555", performance = 0.5f, rating = 3))
                    employeeDao.insertEmployee(Employee(name = "John Doe", email = "john.d@example.com", designation = "Android Developer", department = "Mobile", phoneNumber = "123-456-7890", performance = 0.9f, rating = 5))
                    employeeDao.insertEmployee(Employee(name = "Jane Smith", email = "jane.s@example.com", designation = "iOS Developer", department = "Mobile", phoneNumber = "123-456-7891", performance = 0.8f, rating = 4))
                    employeeDao.insertEmployee(Employee(name = "Peter Jones", email = "peter.j@example.com", designation = "Web Developer", department = "Web", phoneNumber = "123-456-7892", performance = 0.7f, rating = 3))
                    employeeDao.insertEmployee(Employee(name = "Mary Johnson", email = "mary.j@example.com", designation = "UI/UX Designer", department = "Design", phoneNumber = "123-456-7893", performance = 0.95f, rating = 5))
                    employeeDao.insertEmployee(Employee(name = "David Williams", email = "david.w@example.com", designation = "QA Engineer", department = "QA", phoneNumber = "123-456-7894", performance = 0.85f, rating = 4))
                    employeeDao.insertEmployee(Employee(name = "Linda Brown", email = "linda.b@example.com", designation = "Project Manager", department = "Management", phoneNumber = "123-456-7895", performance = 0.92f, rating = 5))
                    employeeDao.insertEmployee(Employee(name = "Robert Davis", email = "robert.d@example.com", designation = "Data Scientist", department = "Data", phoneNumber = "123-456-7896", performance = 0.88f, rating = 4))
                    employeeDao.insertEmployee(Employee(name = "Patricia Miller", email = "patricia.m@example.com", designation = "DevOps Engineer", department = "Infrastructure", phoneNumber = "123-456-7897", performance = 0.91f, rating = 5))
                    employeeDao.insertEmployee(Employee(name = "Michael Wilson", email = "michael.w@example.com", designation = "Software Engineer", department = "Engineering", phoneNumber = "123-456-7898", performance = 0.78f, rating = 3))
                    employeeDao.insertEmployee(Employee(name = "Barbara Moore", email = "barbara.m@example.com", designation = "Product Manager", department = "Management", phoneNumber = "123-456-7899", performance = 0.93f, rating = 5))

                    // Get employees for task assignment
                    val testEmployee = employeeDao.getEmployeeByName("Test Employee")
                    val johnDoe = employeeDao.getEmployeeByName("John Doe")
                    val janeSmith = employeeDao.getEmployeeByName("Jane Smith")
                    val peterJones = employeeDao.getEmployeeByName("Peter Jones")

                    // Insert sample tasks
                    if(testEmployee != null) {
                        taskDao.insertTask(Task(title = "Review test plan", employeeId = testEmployee.id))
                    }
                    if (johnDoe != null) {
                        taskDao.insertTask(Task(title = "Fix login bug", employeeId = johnDoe.id))
                        taskDao.insertTask(Task(title = "Write API documentation", employeeId = johnDoe.id))
                    }
                    if (janeSmith != null) {
                        taskDao.insertTask(Task(title = "Implement new feature", employeeId = janeSmith.id))
                        taskDao.insertTask(Task(title = "Create a design mockup", employeeId = janeSmith.id, isCompleted = true))
                    }
                    if (peterJones != null) {
                        taskDao.insertTask(Task(title = "Update website", employeeId = peterJones.id))
                        taskDao.insertTask(Task(title = "Test new feature", employeeId = peterJones.id, isCompleted = true))
                    }

                    // ✅ Insert sample activities
                    activityDao.insertActivity(
                        Activity(
                            title = "John Doe joined the team",
                            description = "New Android Developer onboarded",
                            type = ActivityType.EMPLOYEE_ADDED,
                            relatedEmployeeId = johnDoe?.id
                        )
                    )
                    activityDao.insertActivity(
                        Activity(
                            title = "Jane completed design mockup",
                            description = "Task marked as completed",
                            type = ActivityType.TASK_COMPLETED,
                            relatedEmployeeId = janeSmith?.id
                        )
                    )
                    activityDao.insertActivity(
                        Activity(
                            title = "Mary's performance updated",
                            description = "Performance rating increased to 95%",
                            type = ActivityType.PERFORMANCE_UPDATED,
                            relatedEmployeeId = employeeDao.getEmployeeByName("Mary Johnson")?.id
                        )
                    )
                }
            }
        }).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideAttendanceRecordDao(database: AppDatabase) = database.attendanceRecordDao()

    @Provides
    fun provideEmployeeDao(database: AppDatabase): EmployeeDao = database.employeeDao()

    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao = database.activityDao() // ✅ NEW
}