package com.example.unscramble.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords


class GameViewModel : ViewModel(){

    //private val _uiState = MutableStateFlow(GameUiState())
    //val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val _uiState: MutableState<GameUiState> = mutableStateOf(GameUiState())

    var userGuess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()

    private lateinit var currentWord: String

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess(){
        if(userGuess.equals(currentWord, ignoreCase = true)){
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        }else{
            _uiState.value = _uiState.value.copy(isGuessedWordWrong = true)

//            _uiState.update { currentState ->
//                currentState.copy(isGuessedWordWrong = true)
//            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game, update isGameOver to true, don't pick a new word
            //_uiState.update { currentState ->
               _uiState.value =  _uiState.value.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            //}
        } else{
            // Normal round in the game
//            _uiState.update { currentState ->
//                currentState.copy(
//                    isGuessedWordWrong = false,
//                    currentScrambledWord = pickRandomWordAndShuffle(),
//                    currentWordCount = currentState.currentWordCount.inc(),
//                    score = updatedScore
//                )
//            }
            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = _uiState.value.currentWordCount.inc(),
                    score = updatedScore
            )
        }
    }

    fun reshuffleWord(){
//        _uiState.update { state ->
//            state.copy( currentScrambledWord = shuffleCurrentWord((state.currentScrambledWord)))
//        }
        _uiState.value = _uiState.value.copy(
            currentScrambledWord = shuffleCurrentWord((_uiState.value.currentScrambledWord))
        )
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

}