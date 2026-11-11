package com.leandro.unibook.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.ui.AttendancePhoto
import com.leandro.unibook.ui.theme.AppColors

private val DetailText = Color(0xFF1E293B)
private val DetailMuted = Color(0xFF64748B)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailsScreen(
    attendance: AttendanceItem,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF9FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ficha do atendimento", fontWeight = FontWeight.Bold)
                        Text(
                            attendance.nome,
                            color = DetailMuted,
                            fontSize = 13.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar ficha")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (attendance.fotoBase64.isNotBlank()) {
                        AttendancePhoto(
                            photoBase64 = attendance.fotoBase64,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    Text(
                        attendance.nome,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        attendance.situacaoNaIgreja
                            .joinToString(" • ") { it.replace('_', ' ').lowercase().replaceFirstChar(Char::uppercase) }
                            .ifEmpty { "Atendimento" },
                        fontSize = 14.sp,
                        color = DetailMuted
                    )
                }
            }

            DetailSection("Dados pessoais") {
                DetailField("Telefone", attendance.telefone)
                DetailField("E-mail", attendance.email)
                DetailField("Endereço", attendance.endereco)
                DetailField("Bairro", attendance.bairro)
            }

            DetailSection("Atendimento") {
                DetailField("Data", attendance.dataAtendimento)
                DetailField("Principal necessidade", attendance.maiorProblema)
                DetailField("Cultos a frequentar", attendance.cultosAFrequentar)
            }

            DetailSection("Acompanhamento pastoral") {
                DetailField("Responsável", attendance.responsavelAcompanhamento)
                DetailField("Visto do pastor", attendance.vistoDoPastor)
            }

            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text("Editar ficha", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = DetailText, fontSize = 17.sp)
            content()
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(label, color = DetailMuted, fontSize = 12.sp)
        Text(
            value.ifBlank { "Não informado" },
            color = DetailText,
            fontSize = 15.sp
        )
    }
}
