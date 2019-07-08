package com.ljb.mvp.kotlin.presenter

import com.ljb.mvp.kotlin.common.LoginUser
import com.ljb.mvp.kotlin.common.rx.subscribeNet
import com.ljb.mvp.kotlin.contract.RepositoriesContract
import com.ljb.mvp.kotlin.protocol.http.IUserHttpProtocol
import com.ljb.mvp.kotlin.common.rx.RxUtils
import com.ljb.mvp.kotlin.contract.StarredContract
import com.ljb.mvp.kotlin.domain.Repository
import com.ljb.mvp.kotlin.model.RepositoriesModel
import com.ljb.mvp.kotlin.model.StarredModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mvp.ljb.kt.presenter.BaseMvpPresenter
import mvp.ljb.kt.presenter.getContextEx
import net.ljb.kt.client.HttpFactory

/**
 * @Author:Kotlin MVP Plugin
 * @Date:2019/04/20
 * @Description input description
 **/
class RepositoriesPresenter : BaseMvpPresenter<RepositoriesContract.IView, RepositoriesContract.IModel>(), RepositoriesContract.IPresenter {

    private var mPage = 1

    override fun registerModel() = RepositoriesModel::class.java

    override fun onLoadMore() {
        mPage++
        getDataFromNet(mPage)
    }

    override fun onRefresh() {
        mPage = 1
        getDataFromNet(mPage)
    }

    private fun getDataFromNet(page: Int) {
        getModel().getRepositories(page)
                .compose(RxUtils.bindToLifecycle(getMvpView()))
                .compose(RxUtils.schedulerIO2Main<MutableList<Repository>>())
                .subscribeNet(getContextEx()) {
                    onNextEx { getMvpView().showPage(it, page) }
                    onErrorEx { getMvpView().errorPage(it, page) }
                }
    }

}