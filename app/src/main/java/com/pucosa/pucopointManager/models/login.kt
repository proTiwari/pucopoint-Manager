package com.pucosa.pucopointManager.models

import android.text.Editable

data class LoginModel(
  var loginEmail: Editable? = null,
  var loginPassword: Editable? = null
)