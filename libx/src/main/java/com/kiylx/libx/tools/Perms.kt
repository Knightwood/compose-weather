package com.kiylx.libx.tools

import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
import com.permissionx.guolindev.request.PermissionBuilder

/*
示例：
   fun requestPermsTest() {
        val perms = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        //前缀函数式调用
        this requestThese perms explainReason null goToSetting null finally { allGranted, grantedList, deniedList ->

        }
        //或者是
        requestThese(perms).explainReason { scope, deniedList ->
        //非必须调用explainReason
        //在用户拒绝权限后，这里会被调用

        }.goToSetting { scope, deniedList ->
        //非必须调用goToSetting
        //当前往设置之前，这里会被调用

        }.finally { allGranted, grantedList, deniedList ->
            //这里是申请的结果
        }

        //可以不调用goToSetting和explainReason
        requestThese(perms).finally { allGranted, grantedList, deniedList ->

        }
*/

infix fun AppCompatActivity.requestThese(
    corePerms: List<String>,
): Pair<PermissionBuilder, AppCompatActivity> {
    return PermissionX.init(this).permissions(corePerms)
        .explainReasonBeforeRequest() to this   //请求权限前解释原因
}

infix fun Pair<PermissionBuilder, AppCompatActivity>.explainReason(
    explainReasonCallback: ExplainReasonCallback?,
): Pair<PermissionBuilder, AppCompatActivity> {
    first.onExplainRequestReason(explainReasonCallback
        ?: ExplainReasonCallback { scope, deniedList -> //PermissionX 提供了 onExplainRequestReason 方法。将此方法链接在请求方法之前，如果用户拒绝其中一个权限，则onExplainRequestReason方法将首先获得回调。
            // 然后，您可以调用showRequestReasonDialog方法来向用户解释为什么需要这些权限，如下所示。
            scope.showRequestReasonDialog(deniedList,
                "这些权限是应用运行的必需权限",
                "确定",
                "取消")
        })//申请权限之前先解释原因

    return this
}

infix fun Pair<PermissionBuilder, AppCompatActivity>.goToSetting(
    forwardToSettingsCallback: ForwardToSettingsCallback?,
): Pair<PermissionBuilder, AppCompatActivity> {
    first.onForwardToSettings(forwardToSettingsCallback
        ?: ForwardToSettingsCallback { scope, deniedList ->
            scope.showForwardToSettingsDialog(deniedList,
                "你需要去设置里允许这些权限",
                "确定",
                "取消")
        })//如果永久拒绝，可以用这个跳转设置页面
    return this
}

inline infix fun Pair<PermissionBuilder, AppCompatActivity>.finally(
    crossinline block: AppCompatActivity.(allGranted: Boolean, grantedList: List<String>, deniedList: List<String>) -> Unit,
) {
    first.request { allGranted, grantedList, deniedList ->
        second.block(allGranted, grantedList, deniedList)
    }
}
