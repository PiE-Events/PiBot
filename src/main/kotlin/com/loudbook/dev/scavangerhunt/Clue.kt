package com.loudbook.dev.scavangerhunt

class Clue(
    val number: Int,
    var message: String,
    var answers: List<String>) {

    var answered = false
    fun isAnswer(answer: String): Boolean {
        for (trueAnswer in answers) {
            if (trueAnswer.equals(answer, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}