package com.leandro.unibook.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.model.toAttendanceDisplayDate
import com.leandro.unibook.attendance.presentation.ui.AttendancePhoto
import com.leandro.unibook.home.presentation.FundoAzulComCurva
import com.leandro.unibook.ui.theme.AppColors
import kotlinx.coroutines.launch

val PaperBlue = AppColors.Primary
private val LineBlue = AppColors.SurfaceSecondary
private val TextDark = AppColors.TextPrimary
private val PaperBg = AppColors.CardBackground

@Composable
fun AttendanceDetailsScreen(
    attendance: AttendanceItem,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: suspend () -> AttendanceResult = { AttendanceResult.Success },
    onDeleted: () -> Unit = {}
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) showDeleteConfirmation = false
            },
            title = { Text("Excluir atendimento?") },
            text = {
                Text(
                    deleteError ?: "O atendimento de ${attendance.nome} será removido do app e do servidor.",
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            when (val result = onDelete()) {
                                AttendanceResult.Success -> onDeleted()
                                is AttendanceResult.Error -> {
                                    deleteError = result.message
                                    isDeleting = false
                                }
                            }
                        }
                    }
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Excluir", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text("Ficha de Atendimento", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Excluir atendimento",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar ficha")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        FundoAzulComCurva{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 10.dp)
            ) {
                // CADERNO PAUTADO COM ESPIRAL
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        // ESPIRAL DO CADERNO (LADO ESQUERDO)
                        SpiralBinding(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(26.dp)
                        )

                        // PÁGINA PAUTADA DO ATENDIMENTO
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 18.dp, end = 16.dp, bottom = 20.dp, start = 6.dp)
                        ) {
                            // CABEÇALHO DO CADERNO: TÍTULO E FOTO
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ATENDIMENTO",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PaperBlue
                                )

                                // MOLDURA DA FOTO 3x4 FIXADA NO CADERNO
                                Box(
                                    modifier = Modifier
                                        .size(width = 70.dp, height = 82.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF0F4F8))
                                        .border(2.dp, PaperBlue, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (attendance.fotoBase64.isNotBlank()) {
                                        AttendancePhoto(
                                            photoBase64 = attendance.fotoBase64,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = "Foto da Pessoa",
                                                tint = PaperBlue.copy(alpha = 0.5f),
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                "FOTO 3x4",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PaperBlue.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // CAMPOS FORMULÁRIO ESTILO CADERNO IMPRESSO
                            NotebookLinedField(label = "Nome:", value = attendance.nome)
                            NotebookLinedField(label = "Endereço:", value = attendance.endereco)
                            NotebookLinedField(label = "Bairro:", value = attendance.bairro)
                            NotebookLinedField(label = "Email:", value = attendance.email)
                            NotebookLinedField(label = "Tel:", value = attendance.telefone)

                            // SITUAÇÃO NA IGREJA (CHECKBOXES)
                            Spacer(Modifier.height(6.dp))
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(
                                    "Situação na Igreja:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PaperBlue
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val statusList = attendance.situacaoNaIgreja.map { it.uppercase() }

                                    NotebookCheck(
                                        label = "1ª Vez",
                                        checked = statusList.contains("PRIMEIRA_VEZ") || statusList.contains("PRIMEIRA VEZ")
                                    )
                                    NotebookCheck(
                                        label = "Afastado",
                                        checked = statusList.contains("AFASTADO")
                                    )
                                    NotebookCheck(
                                        label = "Membro",
                                        checked = statusList.contains("MEMBRO")
                                    )
                                    NotebookCheck(
                                        label = "Grupo",
                                        checked = statusList.contains("GRUPO")
                                    )
                                }
                                if (attendance.nomeDoGrupo.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    NotebookLinedField(label = "Nome do Grupo:", value = attendance.nomeDoGrupo)
                                }
                                Spacer(Modifier.height(4.dp))
                                HorizontalDivider(thickness = 1.dp, color = LineBlue)
                            }

                            Spacer(Modifier.height(12.dp))

                            // SEÇÃO 1: Qual é o seu maior problema?
                            NotebookMultiLineSection(
                                title = "Qual é o seu maior problema?",
                                value = attendance.maiorProblema
                            )

                            Spacer(Modifier.height(12.dp))

                            // SEÇÃO 2: Os cultos que devem ser freqüentados
                            NotebookMultiLineSection(
                                title = "Os cultos que devem ser freqüentados:",
                                value = attendance.cultosAFrequentar
                            )

                            Spacer(Modifier.height(12.dp))

                            // SEÇÃO 3: Responsável pelo acompanhamento
                            NotebookLinedField(
                                label = "Responsável pelo acompanhamento:",
                                value = attendance.responsavelAcompanhamento
                            )

                            Spacer(Modifier.height(10.dp))

                            // SEÇÃO RODAPÉ: Atendimento e Visto do Pastor
                            NotebookLinedField(
                                label = "Data do Atendimento:",
                                value = attendance.dataAtendimento.toAttendanceDisplayDate()
                            )
                            NotebookLinedField(
                                label = "Visto do Pastor:",
                                value = attendance.vistoDoPastor
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // BOTÕES DE AÇÃO DA FICHA
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar Ficha de Atendimento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Excluir Atendimento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

    }
}

@Composable
private fun SpiralBinding(modifier: Modifier = Modifier, count: Int = 18) {
    Canvas(modifier = modifier) {
        val loopHeight = size.height / count
        for (i in 0 until count) {
            val y = i * loopHeight + loopHeight / 2

            // Furo no papel
            drawCircle(
                color = Color(0xFF2B2B2B),
                radius = 2.5.dp.toPx(),
                center = Offset(12.dp.toPx(), y)
            )

            // Espiral de ferro
            drawRoundRect(
                color = Color(0xFF1E1E1E),
                topLeft = Offset(2.dp.toPx(), y - 5.dp.toPx()),
                size = Size(16.dp.toPx(), 10.dp.toPx()),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun NotebookLinedField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PaperBlue
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = value.ifBlank { "──────────" },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(2.dp))
        HorizontalDivider(thickness = 1.dp, color = LineBlue)
    }
}

@Composable
private fun NotebookMultiLineSection(
    title: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = PaperBlue
        )
        Spacer(Modifier.height(4.dp))

        val textToDisplay = value.ifBlank { "Nenhum detalhe informado" }
        Text(
            text = textToDisplay,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = LineBlue)
    }
}

@Composable
private fun NotebookCheck(
    label: String,
    checked: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = PaperBlue,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PaperBlue
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AttendanceDetailsScreenPreview() {
    MaterialTheme {
        AttendanceDetailsScreen(
            attendance = AttendanceItem(
                id = "1",
                nome = "João da Silva",
                endereco = "Rua das Flores, 123",
                bairro = "Centro",
                telefone = "(11) 91234-5678",
                email = "joao.silva@email.com",
                situacaoNaIgreja = listOf("MEMBRO"),
                maiorProblema = "Problema de saúde e busca por fortalecimento espiritual.",
                cultosAFrequentar = "Domingo às 18h e Quarta-feira às 19h30.",
                responsavelAcompanhamento = "Pastor José",
                dataAtendimento = "2024-06-15",
                vistoDoPastor = "Visto pelo Pastor",
                fotoBase64 = ""
            ),
            onBack = {},
            onEdit = {},
            onDelete = { AttendanceResult.Success }
        )
    }
}
