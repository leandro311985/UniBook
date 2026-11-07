package com.leandro.unibook.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.ui.theme.AppColors

private val Gold = Color(0xFFE5A51B)
private val Background = Color(0xFFF9FAFC)
private val LightBlue = Color(0xFFEFF4FF)
private val TextDark = Color(0xFF1E293B)
private val TextMuted = Color(0xFF64748B)

@Composable
fun HomeScreen(onAttendanceClick: () -> Unit = {}) {
    HomeScreenContent(onAttendanceClick = onAttendanceClick)
}

@Composable
fun HomeScreenContent(onAttendanceClick: () -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    "Início" to Icons.Default.Home,
                    "Atendimentos" to Icons.Default.VolunteerActivism,
                    "Notificações" to Icons.Outlined.Notifications,
                    "Perfil" to Icons.Outlined.Person
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            if (index == 1) {
                                onAttendanceClick()
                            }
                        },
                        icon = {
                            Icon(item.second, contentDescription = item.first)
                        },
                        label = { Text(item.first, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppColors.Primary,
                            selectedTextColor = AppColors.Primary,
                            indicatorColor = LightBlue,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // Top Bar (Notificação, Local da Igreja, Perfil do Servo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão de Notificação
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = TextDark
                        )
                    }
                }

                // Localização / Congregação
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp)
                ) {
                    Text(
                        "Sua congregação",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            "Igreja Central - Sede",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Perfil do Servo / Obreiro
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✝", color = Gold, fontSize = 24.sp)
                }
            }

            // Headline
            Text(
                "Acolhimento e\nCuidado Pastoral.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 34.sp,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Campo de Busca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = {
                    Text("Buscar atendimento, fiel ou visita...", color = Color.Gray, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray)
                },
                shape = RoundedCornerShape(30.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = AppColors.Primary
                )
            )

            Spacer(Modifier.height(24.dp))

            // Seção: Tipos de Atendimento (Top Categories)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tipos de Atendimento",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                TextTextButton(text = "Ver Todos") { }
            }

            Spacer(Modifier.height(12.dp))

            val categories = listOf(
                CategoryItem("Primeira Vez", Icons.Default.Favorite, AppColors.Primary),
                CategoryItem("Afastado(a)", Icons.Default.PersonSearch, Gold),
                CategoryItem("Membro", Icons.Default.Groups, AppColors.Primary),
                CategoryItem("Oração", Icons.Default.VolunteerActivism, Gold)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    Card(
                        onClick = onAttendanceClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                category.icon,
                                contentDescription = null,
                                tint = category.color,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                category.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            // Seção: Atendimentos Recentes / Acompanhamento
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Atendimentos Recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                TextTextButton(text = "Ver Todos") { }
            }

            Spacer(Modifier.height(12.dp))

            // Cards estilo banner de atendimentos
            val sampleAtendimentos = listOf(
                RecentAttendance(
                    nome = "Maria Silva",
                    tag = "PRIMEIRA_VEZ",
                    detalhe = "Rua das Flores, 123 - Centro",
                    data = "07/11/2026",
                    responsavel = "João"
                ),
                RecentAttendance(
                    nome = "Carlos Oliveira",
                    tag = "AFASTADO",
                    detalhe = "Pedido de oração e acolhimento",
                    data = "06/11/2026",
                    responsavel = "Pr. André"
                )
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleAtendimentos) { item ->
                    Card(
                        onClick = onAttendanceClick,
                        modifier = Modifier
                            .width(280.dp)
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = LightBlue,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.tag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = item.data,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = item.nome,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Text(
                                text = item.detalhe,
                                fontSize = 13.sp,
                                color = TextMuted
                            )

                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Resp: ${item.responsavel}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextDark
                                )

                                Button(
                                    onClick = onAttendanceClick,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppColors.Primary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Acompanhar", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Banner de Ação de Registro de Atendimento
            Card(
                onClick = onAttendanceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Gold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Novo Atendimento",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Registre pessoas, orações e acolhimentos pastorais.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TextTextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.Primary,
        modifier = Modifier.clickable { onClick() }
    )
}

private data class CategoryItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

private data class RecentAttendance(
    val nome: String,
    val tag: String,
    val detalhe: String,
    val data: String,
    val responsavel: String
)
