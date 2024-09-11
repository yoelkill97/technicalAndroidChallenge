package com.example.technicalchallenge.presentation.login

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.technicalchallenge.R
import com.example.technicalchallenge.presentation.components.CodeValidationDialog
import com.example.technicalchallenge.presentation.components.LoadingScreen
import com.example.technicalchallenge.presentation.components.PhoneInputCode
import com.example.technicalchallenge.util.colorBackground
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider

@Preview
@Composable
fun SingInPreview() {
    SignInScreen()
}

@Composable
fun SingInPage(
    onNavigateToRegister: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel(),
) {
    val authState = viewModel.authState.collectAsState()
    val context: Context = LocalContext.current
    var storedVerificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var showModalValidationPhone by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = authState.value) {
        when (val state = authState.value) {
            is AuthState.Success -> {

                Log.d(
                    "TAG",
                    "firebaseAuth: ${state.authResult.user?.displayName}"
                )
                if(state.authResult.additionalUserInfo?.isNewUser == true) {
                    Toast.makeText(
                        context,
                        "Sign In ${state.authResult.user?.displayName}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                onNavigateToRegister()
                viewModel.loginEventConsumed()

            }

            is AuthState.CodeSent -> {
                Log.d(
                    "TAG",
                    "SendCode: ${state.verificationId}"
                )
                storedVerificationId = state.verificationId
                resendToken = state.resendToken
                showModalValidationPhone = true

            }

            is AuthState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
            }

            else -> {
            }
        }
    }

    SignInScreen(
        authState = authState.value,
        onLogin = { viewModel.signWhitFirebase(it) },
        onValidationPhoneNumber = { viewModel.phoneVerification(it, context as Activity) },
        showModalValidationPhone = viewModel.showModalValidationPhone.value,
        storedVerificationId = viewModel.storedVerificationId.value,
        onChangeshowModalValidationPhone = { showModalValidationPhone = it })
}

@Composable
fun SignInScreen(
    authState: AuthState = AuthState.Idle,
    onLogin: (AuthCredential) -> Unit = {},
    onValidationPhoneNumber: (String) -> Unit = {},
    showModalValidationPhone: Boolean = false,
    storedVerificationId: String = "",
    onChangeshowModalValidationPhone: (Boolean) -> Unit = {}
) {
    val context: Context = LocalContext.current
    val callbackManager = remember { CallbackManager.Factory.create() }
    val googleSignInClient = remember { getGoogleSignInClient(context) }
    var codeInput by remember { mutableStateOf("") }


    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            callbackManager.onActivityResult(it.resultCode, it.resultCode, it.data)
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                onLogin(credential)
            } catch (e: ApiException) {
                // Handle error
                Log.d("TAG", "firebaseAuthWithGoogle: ${e.localizedMessage}")
            }

        }

    fun loginManagerFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
            context as Activity, listOf("email", "public_profile")
        )
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onCancel() {

                    Log.d("TAG", "Facebook Login Canceled")
                }

                override fun onError(error: FacebookException) {

                    Log.d("TAG", "Facebook Login Error")
                }

                override fun onSuccess(result: LoginResult) {

                    val credential =
                        FacebookAuthProvider.getCredential(result.accessToken.token)
                    onLogin(credential)
                }


            })
    }
    /*    val facebookLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val result = FacebookSdk.getCallbackManager().onActivityResult(it.resultCode, it.data)
            if (result) {
                // Handle Facebook login result
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    val credential = FacebookAuthProvider.getCredential(accessToken.token)
                    viewModel.signWhitFacebookCredential(credential) {
                        Log.d("TAG", "firebaseAuthWithFacebook: ${accessToken.userId}")
                    }
                }
            }
        }*/

    Column(
        modifier = Modifier
            .background(colorBackground)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_android),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.18f)
        )
        //PhoneVerificationButton(viewModel, auth)
        PhoneInputCode(onVerificationCode = { onValidationPhoneNumber(it) })
        CodeValidationDialog(
            codeInput = codeInput,
            isShowingDialog = showModalValidationPhone,
            onDismiss = {onChangeshowModalValidationPhone(!showModalValidationPhone)} ,
            onCodeChange = { codeInput = it },
            onValidate = { code ->
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                onLogin(credential)
            },
        )
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign-In",
                    tint = Color.Unspecified, // Sin tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign in with Google",
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Facebook Sign-In Button
        Button(
            onClick = { loginManagerFacebook() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook Sign-In",
                    tint = Color.Unspecified, // Sin tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign in with Facebook",
                    color = Color.White // Texto en blanco
                )
            }
        }
    }
    LoadingScreen(isShowingDialog = authState is AuthState.Loading)


}

// Function to get Google Sign-In Client
fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}
