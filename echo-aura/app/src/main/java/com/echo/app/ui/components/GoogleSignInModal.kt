package com.echo.app.ui.components

import android.accounts.AccountManager
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.echo.app.auth.GoogleAccountInfo
import com.echo.app.auth.GoogleAuthManager
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSignInModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSignInSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val deviceAccounts by viewModel.deviceGoogleAccounts.collectAsState()
    val isAuthenticating by viewModel.isAuthenticatingGoogle.collectAsState()
    val authError by viewModel.googleAuthError.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Activity result launcher for native Android Google Account Picker Intent
    val systemAccountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val chosenAccountEmail = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!chosenAccountEmail.isNullOrEmpty()) {
                val namePart = chosenAccountEmail.substringBefore("@")
                    .replace(".", " ")
                    .split(" ")
                    .filter { it.isNotEmpty() }
                    .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }

                val authenticatedAcc = GoogleAccountInfo(
                    email = chosenAccountEmail,
                    displayName = if (namePart.isNotBlank()) namePart else chosenAccountEmail.substringBefore("@"),
                    photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                )
                viewModel.signInWithGoogleAccount(authenticatedAcc)
                onSignInSuccess()
                onDismiss()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDeviceGoogleAccounts(context)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PitchBlack,
        contentColor = PureWhite,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("google_signin_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Google Branding Icon Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PureWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = PitchBlack
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "SIGN IN WITH GOOGLE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Choose or sign into a Google Account on this device",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("google_modal_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Neutral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (authError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AccentFire)
                        .background(AccentFire.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "AUTH NOTICE: ${authError}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = AccentFire
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isAuthenticating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PureWhite)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Authenticating Google Account...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                }
            } else {
                Text(
                    text = if (deviceAccounts.isNotEmpty()) "// ACCOUNTS REGISTERED ON DEVICE (${deviceAccounts.size})" else "// SYSTEM GOOGLE AUTHENTICATION",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (deviceAccounts.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(deviceAccounts) { acc ->
                            val isSelected = userProfile?.email == acc.email && userProfile?.isGoogleSignedIn == true

                            GoogleAccountCard(
                                account = acc,
                                isSelected = isSelected,
                                onSelect = {
                                    viewModel.signInWithGoogleAccount(acc)
                                    onSignInSuccess()
                                    onDismiss()
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Launch System Account Chooser
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureWhite)
                        .background(DarkNeutral900)
                        .clickable {
                            try {
                                systemAccountPickerLauncher.launch(
                                    GoogleAuthManager.createGoogleAccountPickerIntent()
                                )
                            } catch (e: Exception) {
                                val activity = context as? Activity
                                if (activity != null) {
                                    GoogleAuthManager.launchCredentialManagerSignIn(
                                        activity = activity,
                                        coroutineScope = coroutineScope,
                                        onSuccess = { googleAcc ->
                                            viewModel.signInWithGoogleAccount(googleAcc)
                                            onSignInSuccess()
                                            onDismiss()
                                        },
                                        onError = { err ->
                                            viewModel.setGoogleAuthError(err)
                                        }
                                    )
                                }
                            }
                        }
                        .padding(14.dp)
                        .testTag("btn_system_google_account_picker"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Google Account",
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LAUNCH SYSTEM GOOGLE ACCOUNT PICKER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Launch Credential Manager Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral800)
                        .background(PitchBlack)
                        .clickable {
                            val activity = context as? Activity
                            if (activity != null) {
                                GoogleAuthManager.launchCredentialManagerSignIn(
                                    activity = activity,
                                    coroutineScope = coroutineScope,
                                    onSuccess = { googleAcc ->
                                        viewModel.signInWithGoogleAccount(googleAcc)
                                        onSignInSuccess()
                                        onDismiss()
                                    },
                                    onError = { err ->
                                        viewModel.setGoogleAuthError(err)
                                    }
                                )
                            }
                        }
                        .padding(12.dp)
                        .testTag("btn_credential_manager_prompt"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Credential Manager",
                            tint = Neutral500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "USE GOOGLE CREDENTIAL MANAGER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun GoogleAccountCard(
    account: GoogleAccountInfo,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) PureWhite else DarkNeutral800
            )
            .background(if (isSelected) DarkNeutral800 else PitchBlack)
            .clickable { onSelect() }
            .padding(12.dp)
            .testTag("google_acc_${account.email}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar / Google Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkNeutral900)
                    .border(1.dp, DarkNeutral800, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!account.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = account.photoUrl,
                        contentDescription = account.displayName,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = account.displayName,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = account.displayName,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.email,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isSelected) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Account",
                    tint = PureWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "[ ACTIVE ]",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .border(1.dp, DarkNeutral800)
                    .background(DarkNeutral900)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SELECT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
