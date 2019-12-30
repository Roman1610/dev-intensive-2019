package ru.skillbranch.devintensive

import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.devintensive.extensions.*
import ru.skillbranch.devintensive.models.*
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testInstance() {
        val user = User("1", "John", "Wick")
    }

    @Test
    fun testUserFactory() {
        val user = User.makeUser("John Wick")
        val user1 = User.makeUser("Wick")
        val user2 = User.makeUser("")
        val user3 = User.makeUser(" ")
        val user4 = User.makeUser(null)

        println("""
            $user
            $user1
            $user2
            $user3
            $user4
        """.trimIndent())
    }

    @Test
    fun testMessageAbstractFactory() {
        val user = User.makeUser("Роман Уваров")

        val message = BaseMessage.makeMessage(user, Chat("0"),
            payload = "any text message", type = "text") // Роман отправил сообщение "any text message" только что
        val message1 = BaseMessage.makeMessage(user, Chat("0"),
            Date().add(-2, TimeUnits.HOUR), payload = "https://anyurl.com",
            type = "image", isIncoming = true) // Роман получил изображение "https://anyurl.com" 2 часа назад

        println(message.formatMessage())
        println(message1.formatMessage())
    }

    @Test
    fun testDateFormat() {
        println(Date().format())
        println(Date().format("HH:mm"))
        println(Date().format("HH:mm:ss"))
    }

    @Test
    fun testDateAdd() {
        val user = User.makeUser("John Wick")
        val user1 = user.copy(lastVisit = Date())
        val user2 = user.copy(lastVisit = Date().add(2, TimeUnits.HOUR))
        val user3 = user.copy(lastVisit = Date().add(4, TimeUnits.DAY))

        print("""
            ${user.lastVisit?.format()}
            ${user1.lastVisit?.format()}
            ${user2.lastVisit?.format()}
            ${user3.lastVisit?.format()}
        """.trimIndent())
    }

    @Test
    fun testToInitials() {
        println(Utils.toInitials("john" ,"doe")) // JD
        println(Utils.toInitials("John", null)) // J
        println(Utils.toInitials(null, null)) // null
        println(Utils.toInitials(" ", "")) // null
    }

    @Test
    fun testTransliteration() {
        println(Utils.transliteration("Женя Стереотипов")) // Zhenya Stereotipov
        println(Utils.transliteration("Amazing Петр","_")) // Amazing_Petr
    }

    @Test
    fun testHumanizeDiff() {
        println(Date().add(-2, TimeUnits.HOUR).humanizeDiff()) // 2 часа назад
        println(Date().add(-5, TimeUnits.DAY).humanizeDiff()) // 5 дней назад
        println(Date().add(2, TimeUnits.MINUTE).humanizeDiff()) // через 2 минуты
        println(Date().add(7, TimeUnits.DAY).humanizeDiff()) // через 7 дней
        println(Date().add(-400, TimeUnits.DAY).humanizeDiff()) // более года назад
        println(Date().add(400, TimeUnits.DAY).humanizeDiff()) // более чем через год
    }

    @Test
    fun testPlural() {
        println(TimeUnits.SECOND.plural(1)) // 1 секунду
        println(TimeUnits.MINUTE.plural(4)) // 4 минуты
        println(TimeUnits.HOUR.plural(19)) // 19 часов
        println(TimeUnits.DAY.plural(222)) // 222 дня
    }

    @Test
    fun testUserBuilder() {
        val user = User.Builder()
            .id("1")
            .firstName("Роман")
            .lastName("Уваров")
            .lastVisit(Date().add(-2, TimeUnits.DAY))
            .build()

        println(user)
    }

    @Test
    fun testStringTruncate() {
        println("Bender Bending Rodriguez — дословно «Сгибальщик Сгибающий Родригес»".truncate()) // Bender Bending R...
        println("Bender Bending Rodriguez — дословно «Сгибальщик Сгибающий Родригес»".truncate(15)) // Bender Bending...
        println("A     ".truncate(3)) // A
    }

    @Test
    fun testStringStripHtml() {
        // Образовательное IT-сообщество Skill Branch
        println("<p class=\"title\">Образовательное IT-сообщество Skill Branch</p>".stripHtml())
        // Образовательное IT-сообщество Skill Branch
        println("<p>Образовательное       IT-сообщество Skill Branch</p>".stripHtml())
    }

    @Test
    fun testValidationOfAnswer() {
        var bender = Bender(question = Bender.Question.NAME)
        assert(bender.validateAnswer("bender") != null)
        assert(bender.validateAnswer("benderq212") != null)
        assert(bender.validateAnswer("Bender12312") == null)
        assert(bender.validateAnswer("Bender") == null)

        bender = Bender(question = Bender.Question.PROFESSION)
        assert(bender.validateAnswer("Bender") != null)
        assert(bender.validateAnswer("benderq212") == null)
        assert(bender.validateAnswer("Bender12312") != null)
        assert(bender.validateAnswer("bender") == null)

        bender = Bender(question = Bender.Question.MATERIAL)
        assert(bender.validateAnswer("Metal123") != null)
        assert(bender.validateAnswer("metalq212") != null)
        assert(bender.validateAnswer("metal12312") != null)
        assert(bender.validateAnswer("metal") == null)
        assert(bender.validateAnswer("Metal") == null)

        bender = Bender(question = Bender.Question.BDAY)
        assert(bender.validateAnswer("Bender") != null)
        assert(bender.validateAnswer("benderq212") != null)
        assert(bender.validateAnswer("Bender12312") != null)
        assert(bender.validateAnswer("2993") == null)
        assert(bender.validateAnswer("3214213") == null)

        bender = Bender(question = Bender.Question.SERIAL)
        assert(bender.validateAnswer("Benderw") != null)
        assert(bender.validateAnswer("bender2") != null)
        assert(bender.validateAnswer("12312") != null)
        assert(bender.validateAnswer("1234567") == null)
        assert(bender.validateAnswer("2716057") == null)
    }

    @Test
    fun testValidateRepository() {
        assert(Utils.isValidRepository("")) //валиден
        assert(Utils.isValidRepository("https://github.com/johnDoe")) //валиден
        assert(Utils.isValidRepository("https://www.github.com/johnDoe")) //валиден
        assert(Utils.isValidRepository("www.github.com/johnDoe")) //валиден
        assert(Utils.isValidRepository("github.com/johnDoe")) //валиден

        assert(!Utils.isValidRepository("https://anyDomain.github.com/johnDoe")) //невалиден
        assert(!Utils.isValidRepository("https://github.com")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/johnDoe/tree")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/johnDoe/tree/something")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/enterprise")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/pricing")) //невалиден
        assert(!Utils.isValidRepository("https://github.com/join")) //невалиден
    }

}
