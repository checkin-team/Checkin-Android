package com.checkin.app.checkin.Home.model

import com.fasterxml.jackson.databind.node.BooleanNode

class TransactionLayoutModel(var restaurantName:String,var locality:String
                             ,var orderId:Double,var cost:Double,var foodStatus:Boolean);