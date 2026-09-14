package com.forexjournal.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.forexjournal.app.data.Trade
import com.forexjournal.app.ui.theme.AppBg
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogTradeDialog(
    onDismiss: () -> Unit,
    onSubmit: (Trade, onResult: (Boolean, String?) -> Unit) -> Unit
) {
    var pair by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("Long") }
    var entry by remember { mutableStateOf("") }
    var exit by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("1") }
    var stopLoss by remember { mutableStateOf("") }
    var takeProfit by remember { mutableStateOf("") }
    var pnl by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var session by remember { mutableStateOf("London") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!saving) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AppPanel),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text("Log Execution", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppText)

                FieldLabel("Pair")
                AppTextField(pair, { pair = it }, placeholder = "EUR/USD")

                FieldLabel("Direction")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleChip("Long", selected = direction == "Long", color = AppGreen) { direction = "Long" }
                    ToggleChip("Short", selected = direction == "Short", color = AppRed) { direction = "Short" }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Entry")
                        AppTextField(entry, { entry = it }, isNumber = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Exit")
                        AppTextField(exit, { exit = it }, isNumber = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Size")
                        AppTextField(size, { size = it }, isNumber = true)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Stop Loss")
                        AppTextField(stopLoss, { stopLoss = it }, isNumber = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Take Profit")
                        AppTextField(takeProfit, { takeProfit = it }, isNumber = true)
                    }
                }

                FieldLabel("Net P&L ($) *")
                AppTextField(pnl, { pnl = it }, isNumber = true, placeholder = "e.g. 250 or -120")

                FieldLabel("Date")
                AppTextField(date, { date = it }, placeholder = "yyyy-MM-dd")

                FieldLabel("Session")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("London", "NY", "Asia").forEach { s ->
                        ToggleChip(s, selected = session == s, color = AppTeal) { session = s }
                    }
                }

                FieldLabel("Notes")
                AppTextField(notes, { notes = it }, placeholder = "Setup reason, what happened...", singleLine = false)

                if (error != null) {
                    Text(error!!, color = AppRed, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancel", color = AppMuted) }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(4.dp))
                    Button(
                        onClick = {
                            val pnlValue = pnl.toDoubleOrNull()
                            if (pair.isBlank() || pnlValue == null) {
                                error = "Pair and a valid Net P&L are required."
                                return@Button
                            }
                            error = null
                            saving = true
                            val trade = Trade(
                                date = date,
                                pair = pair.trim(),
                                direction = direction,
                                entry = entry.toDoubleOrNull(),
                                exit = exit.toDoubleOrNull(),
                                size = size.toDoubleOrNull(),
                                stopLoss = stopLoss.toDoubleOrNull(),
                                takeProfit = takeProfit.toDoubleOrNull(),
                                pnl = pnlValue,
                                session = session,
                                notes = notes.trim()
                            )
                            onSubmit(trade) { success, err ->
                                saving = false
                                if (success) {
                                    onDismiss()
                                } else {
                                    error = "Could not save — ${err ?: "unknown error"}. Make sure the Apps Script was redeployed after adding doPost."
                                }
                            }
                        },
                        enabled = !saving,
                        colors = ButtonDefaults.buttonColors(containerColor = AppTeal, contentColor = AppBg)
                    ) {
                        Text(if (saving) "Saving…" else "Commit to Sheet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 9.sp,
        color = AppMuted,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isNumber: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, color = AppMuted, fontSize = 12.sp) },
        singleLine = singleLine,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = AppText),
        keyboardOptions = if (isNumber) {
            androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        } else androidx.compose.foundation.text.KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTeal,
            unfocusedBorderColor = AppMuted.copy(alpha = 0.4f),
            focusedTextColor = AppText,
            unfocusedTextColor = AppText
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f), contentColor = color),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(label, fontSize = 12.sp, color = AppMuted)
        }
    }
}
