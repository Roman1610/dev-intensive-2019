package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    var amountOfWrongAnswers = 0

    fun validateAnswer(answer: String): String? = when (question) {
        Question.NAME -> Question.NAME.validateAnswer(answer)
        Question.PROFESSION -> Question.PROFESSION.validateAnswer(answer)
        Question.MATERIAL -> Question.MATERIAL.validateAnswer(answer)
        Question.BDAY -> Question.BDAY.validateAnswer(answer)
        Question.SERIAL -> Question.SERIAL.validateAnswer(answer)
        Question.IDLE -> Question.IDLE.validateAnswer(answer)
    }

    fun askQuestion(): String  = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        if (question == Question.IDLE) {
            return "На этом все, вопросов больше нет" to status.color
        }

        val valid = validateAnswer(answer)

        if (valid == null) {
            val localAnswer = answer.toLowerCase()
            if (question.answers.contains(localAnswer)) {
                question = question.nextQuestion()
                return "Отлично - ты справился\n${question.question}" to status.color
            } else {
                amountOfWrongAnswers++
                if (amountOfWrongAnswers > 3) {
                    status = Status.NORMAL
                    question = Question.NAME
                    amountOfWrongAnswers = 0
                    return "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                }
                status = status.nextStatus()
                return "Это неправильный ответ\n${question.question}" to status.color
            }
        } else {
            return "$valid\n${question.question}" to status.color
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)) ,
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (ordinal < values().lastIndex) {
                values()[ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender"))  {
            override fun nextQuestion(): Question = PROFESSION
            override fun validateAnswer(answer: String): String? {
                return if (!answer.contains(regex = Regex("^[A-ZА-Я]\\w+"))) {
                    "Имя должно начинаться с заглавной буквы"
                } else {
                    null
                }
            }
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validateAnswer(answer: String): String? {
                return if (!answer.contains(regex = Regex("^[a-zа-я]\\w+"))) {
                    "Профессия должна начинаться со строчной буквы"
                } else {
                    null
                }
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
            override fun validateAnswer(answer: String): String? {
                return if (answer.contains(regex = Regex("\\d+"))) {
                    "Материал не должен содержать цифр"
                } else {
                    null
                }
            }
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
            override fun validateAnswer(answer: String): String? {
                return if (answer.contains(regex = Regex("[a-zа-я]+", option = RegexOption.IGNORE_CASE))) {
                    "Год моего рождения должен содержать только цифры"
                } else {
                    null
                }
            }
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
            override fun validateAnswer(answer: String): String? {
                return if (answer.length != 7
                    || answer.contains(regex = Regex("[a-zа-я]+", option = RegexOption.IGNORE_CASE))) {
                    "Серийный номер содержит только цифры, и их 7"
                } else {
                    null
                }
            }
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
            override fun validateAnswer(answer: String): String? {
                return null
            }
        };

        abstract fun nextQuestion(): Question
        abstract fun validateAnswer(answer: String): String?

    }

}