package com.checkin.app.checkin.manager.models

data class ItemCustomizationModel(
    var customizationGroupModel: CustomizationGroupModel = CustomizationGroupModel(),
    var listOfFields: ArrayList<CustomizationFieldModel> = ArrayList()
)
