package com.checkin.app.checkin;

import com.airbnb.epoxy.EpoxyDataBindingPattern;
import com.airbnb.epoxy.PackageModelViewConfig;
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyModel;

@EpoxyDataBindingPattern(rClass = R.class, layoutPrefix = "epoxy_item")


@PackageModelViewConfig(defaultBaseModelClass = BaseEpoxyModel.class, rClass = R.class)
interface EpoxyConfig {
}
