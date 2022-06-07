package com.example.vt6002_assignment_tamtakwa217011259

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

class paypalConfig: Application() {
    override fun onCreate() {
        super.onCreate()
        val config = CheckoutConfig(
            application = this,
            clientId = "ASrV-NIq_IKyhqzwF8PuiTmnq-hYkFqt48-J8ri-2l7ooBKqMr0V_B8gDU_4C9Xf9qseJ7DOXt-FezMn",
            environment = Environment.SANDBOX,
            returnUrl = "com.example.paypal://paypalpay",
            currencyCode = CurrencyCode.HKD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
    }
}