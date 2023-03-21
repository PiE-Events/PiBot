package com.loudbook.dev.scavangerhunt

class Clue(
    val number: Int,
    val message: String,
    val answers: List<String>) {

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