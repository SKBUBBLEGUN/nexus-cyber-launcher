package com.nexus.cyberlauncher

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BG = Color(0xFF07090D)
private val CARD = Color(0xFF111720)
private val CARD2 = Color(0xFF19222D)
private val RED = Color(0xFFFF3155)
private val CYAN = Color(0xFF42E8F5)
private val DIM = Color(0xFF8C9AA9)
private val FG = Color(0xFFF0F5F8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 9, 13)
        window.navigationBarColor = android.graphics.Color.rgb(7, 9, 13)
        setContent { NexusApp() }
    }
}
private data class AppInfo(val label: String, val pkg: String, val icon: android.graphics.drawable.Drawable)

@Composable private fun NexusApp() {
    val ctx = LocalContext.current
    var page by remember { mutableStateOf("BASE") }
    var query by remember { mutableStateOf("") }
    var search by remember { mutableStateOf(false) }
    var cyan by remember { mutableStateOf(false) }
    var compact by remember { mutableStateOf(true) }
    var showHelp by remember { mutableStateOf(false) }
    var widgets by remember { mutableStateOf(setOf("BATERIA", "REDE", "DATA")) }
    var log by remember { mutableStateOf(listOf("NÚCLEO NEXUS ONLINE", "INTERFACE HUD INICIALIZADA")) }
    val accent = if (cyan) CYAN else RED
    val battery = remember { (ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100) }
    val clock = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val date = SimpleDateFormat("EEE, dd MMM yyyy", Locale("pt", "BR")).format(Date()).uppercase(Locale("pt", "BR"))
    val apps = remember {
        ctx.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { ctx.packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .map { AppInfo(it.loadLabel(ctx.packageManager).toString(), it.packageName, it.loadIcon(ctx.packageManager)) }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }
    val notificationRequest = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        log = (listOf(if (ok) "PERMISSÃO DE NOTIFICAÇÕES ATIVA" else "PERMISSÃO NÃO CONCEDIDA") + log).take(5)
    }
    MaterialTheme(colorScheme = darkColorScheme(background = BG, surface = CARD, primary = accent)) {
        Column(Modifier.fillMaxSize().background(BG).systemBarsPadding().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("NEXUS", color = FG, fontSize = 30.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, letterSpacing = 3.sp)
                    Text("CYBERNETIC INTERFACE // REDLINE", color = accent, fontSize = 8.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(clock, color = accent, fontSize = 26.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text(date, color = DIM, fontSize = 8.sp)
                    Text("BAT " + battery + "%", color = FG, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
            HorizontalDivider(color = accent.copy(alpha = .65f))
            if (search) OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("PESQUISAR APP…", color = DIM) }, trailingIcon = { IconButton(onClick = { search = false; query = "" }) { Icon(Icons.Default.Close, null, tint = accent) } })
            AnimatedContent(targetState = page, label = "page", modifier = Modifier.weight(1f)) { p ->
                when (p) {
                    "BASE" -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Panel(accent, "STATUS DO SISTEMA", Icons.Default.Memory, Modifier.weight(1f)) {
                                Text("● ONLINE", color = CYAN, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("NÚCLEO ESTÁVEL", color = FG, fontSize = 9.sp)
                                Text("REDE: " + network(ctx), color = DIM, fontSize = 9.sp)
                            }
                            Panel(accent, "ATALHOS", Icons.Default.Bolt, Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Quick(Icons.Default.Search, "BUSCA", accent) { search = true; page = "APPS" }
                                    Quick(Icons.Default.Notifications, "ALERTAS", accent) { page = "ALERTAS" }
                                    Quick(Icons.Default.Settings, "AJUSTES", accent) { page = "AJUSTES" }
                                }
                            }
                        }
                        Panel(accent, "MÓDULOS // WIDGETS", Icons.Default.Dashboard) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                widgets.forEach { w ->
                                    Column(Modifier.weight(1f).background(CARD2, RoundedCornerShape(7.dp)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(when (w) { "BATERIA" -> Icons.Default.BatteryChargingFull; "REDE" -> Icons.Default.Wifi; else -> Icons.Default.CalendarMonth }, null, tint = accent)
                                        Text(when (w) { "BATERIA" -> battery.toString() + "%"; "REDE" -> network(ctx); else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date()) }, color = FG, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                        Text(w, color = DIM, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                if (widgets.isEmpty()) Text("ADICIONE WIDGETS EM AJUSTES", color = DIM, fontSize = 9.sp)
                            }
                        }
                        Panel(accent, "PAINEL TÁTICO", Icons.Default.Tune) {
                            Action("ABRIR APP MATRIX", Icons.Default.Apps, accent, Modifier.fillMaxWidth()) { page = "APPS" }
                            Action("CENTRAL DE NOTIFICAÇÕES", Icons.Default.NotificationsActive, accent, Modifier.fillMaxWidth()) { page = "ALERTAS" }
                            Action("PERSONALIZAR HUD", Icons.Default.Palette, accent, Modifier.fillMaxWidth()) { page = "AJUSTES" }
                        }
                        Panel(accent, "REGISTRO DE OPERAÇÕES", Icons.Default.Terminal) {
                            log.take(if (compact) 2 else 5).forEach { Text("› " + it, color = DIM, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            Text(if (compact) "EXPANDIR LOG +" else "RECOLHER LOG −", color = accent, fontSize = 8.sp, modifier = Modifier.clickable { compact = !compact })
                        }
                    }
                    "APPS" -> Column(Modifier.fillMaxSize()) {
                        Text("NEXUS // APP MATRIX", color = accent, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text(apps.size.toString() + " APLICATIVOS • TOQUE PARA INICIAR", color = DIM, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Spacer(Modifier.height(8.dp))
                        LazyVerticalGrid(columns = GridCells.Adaptive(92.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(apps.filter { it.label.contains(query, true) || it.pkg.contains(query, true) }, key = { it.pkg }) { app ->
                                Column(Modifier.background(CARD, CutCornerShape(10.dp)).border(1.dp, accent.copy(alpha = .3f), CutCornerShape(10.dp)).clickable { ctx.packageManager.getLaunchIntentForPackage(app.pkg)?.let(ctx::startActivity) }.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    val bmp = remember(app.pkg) { drawableBitmap(app.icon) }
                                    Image(bmp.asImageBitmap(), contentDescription = app.label, modifier = Modifier.size(38.dp))
                                    Text(app.label.uppercase(Locale.getDefault()), color = FG, fontSize = 8.sp, fontFamily = FontFamily.Monospace, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                                    Text("INICIAR ↗", color = accent, fontSize = 7.sp)
                                }
                            }
                        }
                    }
                    "ALERTAS" -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("CENTRAL DE ALERTAS", color = accent, fontSize = 19.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("EVENTOS DO LAUNCHER", color = DIM, fontSize = 9.sp)
                        Panel(accent, "LOG DE OPERAÇÕES", Icons.Default.Notifications) {
                            log.forEach { Text("▸ " + it, Modifier.fillMaxWidth().background(CARD2, RoundedCornerShape(6.dp)).padding(10.dp), color = FG, fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                            Action("LIMPAR REGISTRO", Icons.Default.Delete, accent, Modifier.fillMaxWidth()) { log = listOf("REGISTRO LIMPO PELO OPERADOR") }
                        }
                        Text("O Android não permite ler notificações privadas sem acesso especial ativado manualmente.", color = DIM, fontSize = 10.sp)
                    }
                    else -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("CONFIGURAÇÕES // HUD", color = accent, fontSize = 19.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Panel(accent, "IDENTIDADE VISUAL", Icons.Default.Palette) {
                            Setting("PALETA CYBER", "Vermelho / ciano") { Switch(checked = cyan, onCheckedChange = { cyan = it }) }
                            Setting("LOG COMPACTO", "Mais espaço na base") { Switch(checked = compact, onCheckedChange = { compact = it }) }
                        }
                        Panel(accent, "WIDGETS DA BASE", Icons.Default.Dashboard) {
                            listOf("BATERIA", "REDE", "DATA").forEach { w -> Setting(w, if (w in widgets) "ATIVO" else "DESATIVADO") { Checkbox(checked = w in widgets, onCheckedChange = { on -> widgets = if (on) widgets + w else widgets - w }, colors = CheckboxDefaults.colors(checkedColor = accent)) } }
                        }
                        Panel(accent, "PERMISSÕES E SISTEMA", Icons.Default.Security) {
                            Action("SOLICITAR NOTIFICAÇÕES", Icons.Default.NotificationsActive, accent, Modifier.fillMaxWidth()) { if (Build.VERSION.SDK_INT >= 33) notificationRequest.launch(Manifest.permission.POST_NOTIFICATIONS) else log = listOf("ANDROID NÃO EXIGE PERMISSÃO NESTA VERSÃO") + log }
                            Action("GUIA DE PERMISSÕES", Icons.Default.Info, accent, Modifier.fillMaxWidth()) { showHelp = true }
                            Action("AJUSTES DO ANDROID", Icons.Default.Settings, accent, Modifier.fillMaxWidth()) { ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                        }
                        Panel(accent, "SOBRE", Icons.Default.Memory) { Text("NEXUS CYBER LAUNCHER 3.0.0", color = FG, fontSize = 10.sp, fontFamily = FontFamily.Monospace); Text("TACTICAL HUD • FLUID TRANSITIONS", color = DIM, fontSize = 8.sp) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().background(CARD, CutCornerShape(12.dp)).border(1.dp, accent.copy(alpha = .55f), CutCornerShape(12.dp)).padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(Triple("BASE", Icons.Default.Home, "BASE"), Triple("APPS", Icons.Default.Apps, "APPS"), Triple("ALERTAS", Icons.Default.Notifications, "ALERTAS"), Triple("AJUSTES", Icons.Default.Settings, "AJUSTES")).forEach { (label, icon, dest) ->
                    Column(Modifier.weight(1f).clickable { page = dest }.background(if (page == dest) accent.copy(alpha = .13f) else Color.Transparent, RoundedCornerShape(8.dp)).padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, null, tint = if (page == dest) accent else DIM, modifier = Modifier.size(19.dp))
                        Text(label, color = if (page == dest) accent else DIM, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = if (page == dest) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            if (showHelp) AlertDialog(onDismissRequest = { showHelp = false }, containerColor = CARD, title = { Text("PERMISSÕES", color = accent, fontFamily = FontFamily.Monospace) }, text = { Text("O Android controla cada acesso. Notificações são solicitadas aqui; acesso a todas as notificações exige ativação manual nas configurações do sistema.", color = FG) }, confirmButton = { TextButton(onClick = { showHelp = false; ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }) { Text("ABRIR AJUSTES", color = accent) } }, dismissButton = { TextButton(onClick = { showHelp = false }) { Text("FECHAR", color = DIM) } })
        }
    }
}
@Composable private fun Panel(accent: Color, title: String, icon: ImageVector, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.background(CARD, CutCornerShape(11.dp)).border(BorderStroke(1.dp, accent.copy(alpha = .4f)), CutCornerShape(11.dp)).padding(11.dp).animateContentSize(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) { Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp)); Text(title, color = accent, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = .4.sp) }
        content()
    }
}
@Composable private fun Quick(icon: ImageVector, label: String, accent: Color, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick).padding(2.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, tint = accent, modifier = Modifier.size(21.dp)); Text(label, color = DIM, fontSize = 7.sp, fontFamily = FontFamily.Monospace) }
}
@Composable private fun Action(label: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.heightIn(min = 40.dp), shape = CutCornerShape(7.dp), border = BorderStroke(1.dp, accent.copy(alpha = .55f)), contentPadding = PaddingValues(9.dp)) { Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(7.dp)); Text(label, color = FG, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1) }
}
@Composable private fun Setting(title: String, sub: String, control: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, color = FG, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold); Text(sub, color = DIM, fontSize = 8.sp) }; control() }
}
private fun network(ctx: Context): String = try {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val n = cm.activeNetwork
    val c = if (n != null) cm.getNetworkCapabilities(n) else null
    when { c == null -> "OFFLINE"; c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WI-FI"; c.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "MÓVEL"; else -> "ONLINE" }
} catch (_: Exception) { "INDISPONÍVEL" }
private fun drawableBitmap(d: android.graphics.drawable.Drawable): android.graphics.Bitmap {
    val w = (if (d.intrinsicWidth > 0) d.intrinsicWidth else 48).coerceAtMost(192)
    val h = (if (d.intrinsicHeight > 0) d.intrinsicHeight else 48).coerceAtMost(192)
    return android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888).also { b -> val c = android.graphics.Canvas(b); d.setBounds(0, 0, w, h); d.draw(c) }
}
