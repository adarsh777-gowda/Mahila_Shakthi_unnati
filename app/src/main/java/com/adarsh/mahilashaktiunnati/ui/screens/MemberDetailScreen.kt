package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    context: android.content.Context,
    memberId: Int,
    viewModel: MemberViewModel,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    val member by viewModel.getMember(memberId).collectAsState(initial = null)
    val savingsList by viewModel.getSavingsForMember(memberId).collectAsState(initial = emptyList())
    val loanList by viewModel.getLoansForMember(memberId).collectAsState(initial = emptyList())

    var amount by remember { mutableStateOf("") }
    var week by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Paid") }
    var statusMenu by remember { mutableStateOf(false) }

    var loanAmount by remember { mutableStateOf("") }
    var loanDate by remember { mutableStateOf("") }

    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editPhoto by remember { mutableStateOf<String?>(null) }

    var showDeleteMember by remember { mutableStateOf(false) }
    var deleteSavingId by remember { mutableStateOf<Int?>(null) }
    var deleteLoanId by remember { mutableStateOf<Int?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        editPhoto = uri?.toString()
    }

    if (showDeleteMember) {
        AlertDialog(
            onDismissRequest = { showDeleteMember = false },
            title = { Text(stringResource(R.string.delete_member)) },
            text = { Text(stringResource(R.string.delete_member_confirmation)) },
            confirmButton = {
                Button(onClick = {
                    member?.let { viewModel.deleteMember(it) }
                    showDeleteMember = false
                    onBack()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                Button(onClick = { showDeleteMember = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    deleteSavingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteSavingId = null },
            title = { Text("Delete savings entry?") },
            confirmButton = {
                Button(onClick = {
                    deleteSavingId?.let { id ->
                        savingsList.find { it.id == id }?.let { saving ->
                            viewModel.deleteSaving(saving)
                        }
                    }
                    deleteSavingId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { deleteSavingId = null }) { Text("Cancel") }
            }
        )
    }

    deleteLoanId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteLoanId = null },
            title = { Text("Delete loan?") },
            confirmButton = {
                Button(onClick = {
                    deleteLoanId?.let { id ->
                        loanList.find { it.id == id }?.let { loan ->
                            viewModel.deleteLoan(loan)
                        }
                    }
                    deleteLoanId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { deleteLoanId = null }) { Text("Cancel") }
            }
        )
    }

    if (showDatePicker) {
        LoanDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { dateStr ->
                loanDate = dateStr
                showDatePicker = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Back")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = member?.let { "${it.name} (${it.phone})" } ?: "Member",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            val m = member
            if (m != null) {
                LaunchedEffect(m) {
                    if (editName.isBlank()) editName = m.name
                    if (editPhone.isBlank()) editPhone = m.phone
                    if (editPhoto == null) editPhoto = m.photoUri
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = editPhoto,
                        contentDescription = "Member photo",
                        modifier = Modifier.size(72.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { photoPicker.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Pick Photo") }
                    Button(
                        onClick = { 
                            member?.let { 
                                viewModel.updateMember(it.copy(
                                    name = editName.trim(),
                                    phone = editPhone.trim(),
                                    photoUri = editPhoto
                                ))
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Save") }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDeleteMember = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Delete Member")
                }
                Button(
                    onClick = { viewModel.shareWhatsAppSummary(context, m) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share WhatsApp Summary")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Text("Add Savings", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = week,
                onValueChange = { week = it },
                label = { Text("Week") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { statusMenu = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Status: $status")
            }
            DropdownMenu(expanded = statusMenu, onDismissRequest = { statusMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Paid") },
                    onClick = { status = "Paid"; statusMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Pending") },
                    onClick = { status = "Pending"; statusMenu = false }
                )
            }
            Button(
                onClick = {
                    val amt = amount.toLongOrNull()
                    if (amt != null && week.isNotBlank()) {
                        viewModel.addSavings(
                            memberId = memberId,
                            amount = amt,
                            week = week.trim(),
                            status = status
                        )
                        amount = ""
                        week = ""
                        status = "Paid"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add Savings") }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Savings History", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(savingsList) { s ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("₹${s.amount}", style = MaterialTheme.typography.bodyLarge)
                        Text(s.week, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(s.status)
                    IconButton(onClick = { deleteSavingId = s.id }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_savings_entry)
                        )
                    }
                }
            }
        }

        if (savingsList.isEmpty()) {
            item { Text("No savings yet.", style = MaterialTheme.typography.bodyMedium) }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Add Loan", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Loan Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = loanDate,
                onValueChange = { loanDate = it },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Pick Date")
            }

            Button(
                onClick = {
                    val amt = loanAmount.toLongOrNull()
                    if (amt != null && loanDate.isNotBlank()) {
                        viewModel.addLoan(memberId, amt, System.currentTimeMillis(), System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000))
                        loanAmount = ""
                        loanDate = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add Loan") }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Loan History", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(loanList) { loan ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val interest = viewModel.calculateInterest(loan.principalAmount)
                    val total = viewModel.calculateTotal(loan.principalAmount)

                    Text("Amount: ₹${loan.principalAmount}")
                    Text("Interest (10%): ₹$interest")
                    Text("Total Payable: ₹$total")
                    Text("Date: ${java.util.Date(loan.disbursementDate)}")
                    Text("Status: ${if (loan.isPaid) "Paid" else "Pending"}")

                    if (!loan.isPaid) {
                        Button(
                            onClick = { viewModel.markLoanPaid(loan.id) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Mark as Paid")
                        }
                    }

                    Button(
                        onClick = { deleteLoanId = loan.id },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.delete_loan_entry))
                    }
                }
            }
        }

        if (loanList.isEmpty()) {
            item { Text("No loans yet.", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val millis = state.selectedDateMillis ?: System.currentTimeMillis()
                val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
                onDateSelected(dateStr)
            }) { Text("OK") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}
