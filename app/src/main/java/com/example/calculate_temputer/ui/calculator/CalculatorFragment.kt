package com.example.calculate_temputer.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.calculate_temputer.R
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorFragment : Fragment() {

    private lateinit var expressionText: TextView
    private lateinit var resultText: TextView

    private var storedValue: BigDecimal? = null
    private var pendingOperator: Char? = null
    private var currentInput: String = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expressionText = view.findViewById(R.id.expression_text)
        resultText = view.findViewById(R.id.result_text)

        val numberButtons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )
        numberButtons.forEach { id ->
            view.findViewById<MaterialButton>(id).setOnClickListener {
                onNumberPressed(view.findViewById<MaterialButton>(id).text.toString())
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_dot).setOnClickListener { onDotPressed() }
        view.findViewById<MaterialButton>(R.id.btn_plus).setOnClickListener { onOperatorPressed('+') }
        view.findViewById<MaterialButton>(R.id.btn_minus).setOnClickListener { onOperatorPressed('-') }
        view.findViewById<MaterialButton>(R.id.btn_multiply).setOnClickListener { onOperatorPressed('*') }
        view.findViewById<MaterialButton>(R.id.btn_divide).setOnClickListener { onOperatorPressed('/') }
        view.findViewById<MaterialButton>(R.id.btn_equals).setOnClickListener { onEqualsPressed() }
        view.findViewById<MaterialButton>(R.id.btn_clear).setOnClickListener { clearAll() }
        view.findViewById<MaterialButton>(R.id.btn_delete).setOnClickListener { deleteLast() }
        view.findViewById<MaterialButton>(R.id.btn_plus_minus).setOnClickListener { toggleSign() }

        updateDisplay()
    }

    private fun onNumberPressed(digit: String) {
        if (currentInput == "0") {
            currentInput = digit
        } else {
            currentInput += digit
        }
        updateDisplay()
    }

    private fun onDotPressed() {
        if (!currentInput.contains(".")) {
            currentInput += if (currentInput.isEmpty()) "0." else "."
        }
        updateDisplay()
    }

    private fun onOperatorPressed(op: Char) {
        if (currentInput.isNotEmpty()) {
            if (storedValue != null && pendingOperator != null) {
                storedValue = calculate(storedValue!!, pendingOperator!!, currentInput.toBigDecimalOrZero())
            } else {
                storedValue = currentInput.toBigDecimalOrZero()
            }
        }
        pendingOperator = op
        currentInput = "0"
        updateDisplay()
    }

    private fun onEqualsPressed() {
        if (storedValue != null && pendingOperator != null) {
            val result = calculate(storedValue!!, pendingOperator!!, currentInput.toBigDecimalOrZero())
            currentInput = formatNumber(result)
            storedValue = null
            pendingOperator = null
        }
        updateDisplay()
    }

    private fun deleteLast() {
        currentInput = if (currentInput.length > 1) {
            currentInput.dropLast(1)
        } else {
            "0"
        }
        updateDisplay()
    }

    private fun clearAll() {
        storedValue = null
        pendingOperator = null
        currentInput = "0"
        updateDisplay()
    }

    private fun toggleSign() {
        currentInput = if (currentInput.startsWith("-")) {
            currentInput.removePrefix("-")
        } else if (currentInput != "0") {
            "-$currentInput"
        } else {
            currentInput
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        val expression = buildString {
            storedValue?.let {
                append(formatNumber(it))
                pendingOperator?.let { op ->
                    append(" $op")
                }
            }
        }
        expressionText.text = expression
        resultText.text = currentInput
    }

    private fun calculate(a: BigDecimal, op: Char, b: BigDecimal): BigDecimal {
        return when (op) {
            '+' -> a.add(b)
            '-' -> a.subtract(b)
            '*' -> a.multiply(b)
            '/' -> if (b.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else a.divide(b, 8, RoundingMode.HALF_UP)
            else -> b
        }
    }

    private fun formatNumber(number: BigDecimal): String {
        return number.stripTrailingZeros().toPlainString()
    }

    private fun String.toBigDecimalOrZero(): BigDecimal = try {
        this.toBigDecimal()
    } catch (e: NumberFormatException) {
        BigDecimal.ZERO
    }
}
