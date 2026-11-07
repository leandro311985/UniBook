package com.leandro.unibook.home

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF10234E)
private val Gold = Color(0xFFE5A51B)
private val Cream = Color(0xFFFFF3DB)
private val Blue = Color(0xFF1559D6)
private val Background = Color(0xFFF8F9FD)

@Composable
fun HomeScreen(onAttendanceClick: () -> Unit = {}) {
    HomeScreenContent(onAttendanceClick = onAttendanceClick)
}

@Composable
fun HomeScreenContent(onAttendanceClick: () -> Unit = {}) {
        var selectedTab by remember { mutableIntStateOf(0) }

        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    val items = listOf(
                        "Início" to Icons.Default.Home,
                        "Oração" to Icons.Default.FavoriteBorder,
                        "Eventos" to Icons.Default.CalendarMonth,
                        "Perfil" to Icons.Default.Person
                    )

                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(item.second, contentDescription = item.first)
                            },
                            label = { Text(item.first) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue,
                                selectedTextColor = Blue,
                                indicatorColor = Color(0xFFEAF0FF),
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

                // CABEÇALHO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Navy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✝", color = Gold, fontSize = 32.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp)
                    ) {
                        Text(
                            "Minha Igreja",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                        Text(
                            "Seja bem-vindo(a)!",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = "Notificações",
                            tint = Navy
                        )
                    }
                }

                // BANNER PRINCIPAL
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Navy,
                                    Color(0xFF263F70),
                                    Color(0xFFE6A72A)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text(
                        "DEUS CUIDA DE VOCÊ",
                        color = Cream,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Deus cuida,\nguia e caminha\ncom você!",
                        fontSize = 29.sp,
                        lineHeight = 35.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "Entregue o seu caminho ao Senhor.\nSalmos 37:5",
                        color = Color.White,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Navy
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Conheça nossa igreja")
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // BUSCA
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder = {
                        Text("Busque por oração, evento ou assunto...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E8F0),
                        focusedBorderColor = Blue
                    )
                )

                Spacer(Modifier.height(26.dp))

                // SERVIÇOS
                SectionTitle("Serviços para você")

                val services = listOf(
                    Triple("Pedidos de\nOração", Icons.Default.Favorite, Gold),
                    Triple("Atendimento e\nAcolhimento", Icons.Default.SupportAgent, Navy),
                    Triple("Eventos da\nIgreja", Icons.Default.CalendarMonth, Navy),
                    Triple("Estudos\nBíblicos", Icons.Default.MenuBook, Navy),
                    Triple("Contribua", Icons.Default.VolunteerActivism, Gold),
                    Triple("Seja um\nMembro", Icons.Default.Groups, Navy)
                )

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    services.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { service ->
                                ServiceCard(
                                    title = service.first,
                                    icon = service.second,
                                    iconColor = service.third,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (service.first.startsWith("Atendimento")) {
                                            onAttendanceClick()
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // PRÓXIMO EVENTO
                SectionTitle("Próximo evento")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Navy
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Gold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "CULTO DE ADORAÇÃO",
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Um momento especial\nna presença de Deus!",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            "Domingo • 19h",
                            color = Color.White
                        )

                        Text(
                            "Templo da Igreja",
                            color = Color.White
                        )

                        Spacer(Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {},
                            border = ButtonDefaults.outlinedButtonBorder,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text("Mais detalhes")
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }

                Spacer(Modifier.height(26.dp))

                // MENSAGEM DO DIA
                SectionTitle("Mensagem do dia")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Cream
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Navy,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text(
                                "Porque para Deus nada é impossível.",
                                color = Navy,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Lucas 1:37",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    @Composable
    fun SectionTitle(title: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(26.dp)
                    .clip(CircleShape)
                    .background(Gold)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
        }
    }

    @Composable
    fun ServiceCard(
        title: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        iconColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Card(
            onClick = onClick,
            modifier = modifier.height(132.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Cream),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    title,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy
                )
            }
        }
    }