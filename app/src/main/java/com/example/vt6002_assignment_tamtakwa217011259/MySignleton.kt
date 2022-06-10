package com.example.vt6002_assignment_tamtakwa217011259

class MySignleton {
    /**
    @Description/Purpose : Used to store the text of the open login page button, so it is convenient
    to change the text displayed on the button after login and logout.
     */
    object openLoginPageOBj{
        var openLoginPageBtn:String = "Sign In"
    }
    /**
    @Description/Purpose : Used to store prices and use at checkout
     */
    object priceMax{
        var priceMax:Int = 0
    }

}