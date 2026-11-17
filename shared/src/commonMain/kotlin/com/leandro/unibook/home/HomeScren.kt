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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.presentation.ui.AttendancePhoto
import com.leandro.unibook.attendance.presentation.ui.CameraPhotoPicker
import com.leandro.unibook.home.presentation.HomeUiState
import com.leandro.unibook.home.presentation.HomeViewModel
import com.leandro.unibook.ui.theme.AppColors
import org.koin.compose.viewmodel.koinViewModel

private val Gold = Color(0xFFE5A51B)
private val Background = Color(0xFFF9FAFC)
private val LightBlue = Color(0xFFEFF4FF)
private val TextDark = Color(0xFF1E293B)
private val TextMuted = Color(0xFF64748B)

@Composable
fun HomeScreen(
    onAttendanceClick: () -> Unit = {},
    onViewAttendance: (AttendanceItem) -> Unit = {},
    onEditAttendance: (AttendanceItem) -> Unit = {}
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Recarrega a lista ao abrir a tela Home
    LaunchedEffect(Unit) {
        viewModel.loadAttendances()
    }

    HomeScreenContent(
        uiState = uiState,
        churchName = viewModel.churchName,
        churchPhotoBase64 = viewModel.churchPhotoBase64,
        userNickname = viewModel.userNickname,
        userMinistry = viewModel.userMinistry,
        onSaveChurch = { name, photo -> viewModel.updateChurchProfile(name, photo) },
        onSaveUserProfile = { nickname, ministry ->
            viewModel.updateUserProfile(nickname, ministry)
        },
        onAttendanceClick = onAttendanceClick,
        onViewAttendance = onViewAttendance,
        onEditAttendance = onEditAttendance,
        onRefresh = { viewModel.loadAttendances() }
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState = HomeUiState.Loading,
    churchName: String = "Igreja Central - Sede",
    churchPhotoBase64: String = "",
    userNickname: String = "",
    userMinistry: String = "",
    onSaveChurch: (name: String, photoBase64: String) -> Unit = { _, _ -> },
    onSaveUserProfile: (nickname: String, ministry: String) -> Unit = { _, _ -> },
    onAttendanceClick: () -> Unit = {},
    onViewAttendance: (AttendanceItem) -> Unit = {},
    onEditAttendance: (AttendanceItem) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSituationFilter by remember { mutableStateOf<String?>(null) }

    // Estados para o modal de personalização da igreja
    var showChurchModal by remember { mutableStateOf(false) }

    // Modal de Personalização da Igreja
    if (showChurchModal) {
        var tempName by remember(churchName) { mutableStateOf(churchName) }
        var tempPhoto by remember(churchPhotoBase64) { mutableStateOf(churchPhotoBase64) }

        AlertDialog(
            onDismissRequest = { showChurchModal = false },
            title = {
                Text(
                    "Personalizar Sua Congregação",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppColors.Primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Foto / Logo da Igreja:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                    Spacer(Modifier.height(8.dp))

                    CameraPhotoPicker(
                        photoBase64 = tempPhoto,
                        onPhotoCaptured = { tempPhoto = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Nome da Igreja / Congregação:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Igreja Batista Central") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = if (tempName.isNotBlank()) tempName.trim() else churchName
                        onSaveChurch(finalName, tempPhoto)
                        showChurchModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChurchModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
        if (selectedTab == 2) {
            UserProfileScreen(
                nickname = userNickname,
                ministry = userMinistry,
                onSave = onSaveUserProfile,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

            // Top Bar (Notificação, Local da Igreja Personalizável, Perfil do Servo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                // Nome da Congregação (Clicável para personalizar)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp)
                        .clickable { showChurchModal = true }
                ) {
                    Text(
                        "Sua congregação",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            churchName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Personalizar igreja",
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Foto / Logo da Igreja (Clicável para personalizar)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary)
                        .clickable { showChurchModal = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (churchPhotoBase64.isNotBlank()) {
                        AttendancePhoto(
                            photoBase64 = churchPhotoBase64,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("✝", color = Gold, fontSize = 24.sp)
                    }
                }
            }

            // Headline
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
            ) {
                Text(
                    "“Levai as cargas uns dos outros e assim cumprireis a lei de Cristo.”",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Gálatas 6:2",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold
                )
            }

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
                TextTextButton(text = "Ver Todos") { selectedSituationFilter = null }
            }

            Spacer(Modifier.height(12.dp))

            val categories = listOf(
                CategoryItem("Primeira Vez", Icons.Default.Favorite, AppColors.Primary, "PRIMEIRA_VEZ"),
                CategoryItem("Afastado(a)", Icons.Default.PersonSearch, Gold, "AFASTADO"),
                CategoryItem("Membro", Icons.Default.Groups, AppColors.Primary, "MEMBRO"),
                CategoryItem("Oração", Icons.Default.VolunteerActivism, Gold)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category.situationFilter != null &&
                        selectedSituationFilter == category.situationFilter
                    Card(
                        onClick = {
                            val filter = category.situationFilter
                            if (filter == null) {
                                onAttendanceClick()
                            } else {
                                selectedSituationFilter =
                                    if (isSelected) null else filter
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) category.color.copy(alpha = 0.1f) else Color.White
                        ),
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

            // Seção: Atendimentos Recentes (Buscados da API GET /api/atendimentos)
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
                TextTextButton(text = "Atualizar", onClick = onRefresh)
            }

            Spacer(Modifier.height(12.dp))

            when (uiState) {
                HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.Primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                is HomeUiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is HomeUiState.Success -> {
                    val attendances = uiState.attendances.filter { item ->
                        val matchesSearch = searchQuery.isBlank() ||
                            item.nome.contains(searchQuery, ignoreCase = true) ||
                            item.maiorProblema.contains(searchQuery, ignoreCase = true)
                        val matchesSituation = selectedSituationFilter?.let { filter ->
                            item.situacaoNaIgreja.any { it.matchesSituation(filter) }
                        } ?: true

                        matchesSearch && matchesSituation
                    }

                    if (attendances.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Nenhum atendimento registrado.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = onAttendanceClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                                ) {
                                    Text("Cadastrar Atendimento", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(attendances) { item ->
                                AttendanceCard(
                                    item = item,
                                    onView = { onViewAttendance(item) },
                                    onEdit = { onEditAttendance(item) }
                                )
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
}

@Composable
private fun UserProfileScreen(
    nickname: String,
    ministry: String,
    onSave: (nickname: String, ministry: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedNickname by remember(nickname) { mutableStateOf(nickname) }
    var editedMinistry by remember(ministry) { mutableStateOf(ministry) }
    var nicknameError by remember { mutableStateOf(false) }
    var profileSaved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        Text(
            "Meu perfil",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Informe como você deseja ser identificado no aplicativo.",
            fontSize = 14.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                OutlinedTextField(
                    value = editedNickname,
                    onValueChange = {
                        editedNickname = it
                        nicknameError = false
                        profileSaved = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nickname *") },
                    placeholder = { Text("Como devemos chamar você?") },
                    singleLine = true,
                    isError = nicknameError,
                    supportingText = if (nicknameError) {
                        { Text("Informe seu nickname.") }
                    } else {
                        null
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = editedMinistry,
                    onValueChange = {
                        editedMinistry = it
                        profileSaved = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Função ou ministério") },
                    placeholder = { Text("Ex.: Pastor, louvor, recepção") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        val cleanNickname = editedNickname.trim()
                        if (cleanNickname.isEmpty()) {
                            nicknameError = true
                            profileSaved = false
                        } else {
                            onSave(cleanNickname, editedMinistry.trim())
                            editedNickname = cleanNickname
                            editedMinistry = editedMinistry.trim()
                            profileSaved = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar perfil")
                }
                if (profileSaved) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Perfil salvo neste dispositivo.",
                        color = AppColors.Primary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceCard(
    item: AttendanceItem,
    onView: () -> Unit,
    onEdit: () -> Unit
) {
    val mainTag = item.situacaoNaIgreja.firstOrNull() ?: "ATENDIMENTO"
    val locationDetail = listOf(item.bairro, item.endereco).filter { it.isNotBlank() }.joinToString(" - ")
        .ifEmpty { item.maiorProblema.ifEmpty { "Sem detalhes" } }

    Card(
        onClick = onView,
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            if (item.fotoBase64.isNotBlank()) {
                AttendancePhoto(
                    photoBase64 = item.fotoBase64,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.height(12.dp))
            }else{
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Foto da Pessoa",
                    tint = PaperBlue.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.height(12.dp))

            }

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
                        text = mainTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = item.dataAtendimento,
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
                text = locationDetail,
                fontSize = 13.sp,
                color = TextMuted,
                maxLines = 2
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.responsavelAcompanhamento.isNotBlank()) "Resp: ${item.responsavelAcompanhamento}" else "Pastor: ${item.vistoDoPastor}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )

                TextButton(onClick = onView) {
                    Text("Ver Ficha", fontSize = 12.sp)
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar ${item.nome}",
                        tint = AppColors.Primary
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HomePreviewTest(){
    HomeScreenContent()
}

@Composable
private fun TextTextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.Primary,
        modifier = Modifier.clickable { onClick() }
    )
}

private data class CategoryItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val situationFilter: String? = null
)

private fun String.matchesSituation(filter: String): Boolean {
    fun normalize(value: String) = value.trim()
        .replace(" ", "_")
        .replace("-", "_")
        .uppercase()

    return normalize(this) == normalize(filter)
}
