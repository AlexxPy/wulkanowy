package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Teacher
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface TeacherDao : BaseDao<Teacher> {

    @Query("SELECT * FROM Teachers WHERE student_id = :studentId AND class_id = :classId")
    fun loadAll(studentId: Int, classId: Int): Maybe<List<Teacher>>
}
