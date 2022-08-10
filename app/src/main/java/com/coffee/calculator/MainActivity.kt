package com.coffee.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffee.calculator.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Calculator()
                }
            }
        }
    }
}

val buttons = arrayOf(
    arrayOf("AC" to LightGray, "+/-" to LightGray, "%" to LightGray, "/" to Orange),
    arrayOf("7" to DarkGray, "8" to DarkGray, "9" to DarkGray, "x" to Orange),
    arrayOf("4" to DarkGray, "5" to DarkGray, "6" to DarkGray, "-" to Orange),
    arrayOf("1" to DarkGray, "2" to DarkGray, "3" to DarkGray, "+" to Orange),
    arrayOf("0" to DarkGray, "." to DarkGray, "=" to Orange)
)

data class CalculatorState(
    val number1: String = "",
    val number2: String = "0",
    val opt: String? = null
)

fun calculate(curState: CalculatorState, input: String): CalculatorState {
    return when (input) {
        "AC" -> curState.copy(number1 = "", number2 = "0", opt = "")
        "." -> curState.copy(number2 = curState.number2 + input)
        "%" -> curState.copy(number2 = "${curState.number1.toFloat() * curState.number2.toFloat() / 100}")
        "+/-" -> curState.copy(number2 = if(curState.number2 == "0") "-0" else "${-curState.number2.toFloat()}")
        in "0".."9" -> curState.copy(number2 = if (curState.number2 == "0") input else if(curState.number2 == "-0") "${- input.toInt()}" else curState.number2 + input)
        in arrayOf("+", "-", "x", "/") -> curState.copy(
            opt = input,
            number2 = "0",
            number1 = curState.number2
        )
        "=" -> when (curState.opt) {
            "+" -> curState.copy(number2 = "${curState.number1.toFloat() + curState.number2.toFloat()}")
            "-" -> curState.copy(number2 = "${curState.number1.toFloat() - curState.number2.toFloat()}")
            "x" -> curState.copy(number2 = "${curState.number1.toFloat() * curState.number2.toFloat()}")
            "/" -> curState.copy(number2 = if (curState.number2 == "0") "错误" else "${curState.number1.toFloat() / curState.number2.toFloat()}")
            else -> curState
        }
        else -> curState
    }
}

@Composable
fun Calculator() {

    var state by remember {
        mutableStateOf(CalculatorState())
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = formatNumber(state.number2),
                fontSize = resizeFont(formatNumber(state.number2)),
                color = Color.White
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            buttons.forEach {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    it.forEach {
                        CalculatorButton(
                            modifier = Modifier
                                .weight(if (it.first == "0") 2f else 1f)
                                .aspectRatio(if (it.first == "0") 2f else 1f)
                                .clip(CircleShape)
                                .background(it.second),
                            it.first
                        ) {
                            state = calculate(state, it.first)
                        }
                    }

                }
            }

        }

    }
}

fun resizeFont(formatNumber: String): TextUnit {
    return if (formatNumber.length < 7) 100.sp else if (formatNumber.length == 7) 90.sp else if (formatNumber.length == 8) 80.sp else 70.sp
}

fun formatNumber(number: String): String {
    return if (number.endsWith(".0")) number.replace(".0", "") else number
}


@Composable
fun CalculatorButton(modifier: Modifier, symbol: String, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, fontSize = 40.sp, color = Color.White)
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    Calculator()
}