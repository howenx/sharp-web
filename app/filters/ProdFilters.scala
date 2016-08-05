package filters

import javax.inject.Inject

import play.api.http.HttpFilters

/**
  * Created by howen on 15/12/28.
  */
class ProdFilters @Inject()(log: LoggingFilter,
//                            tls:TLSFilter,
                            gzip: play.filters.gzip.GzipFilter
                       ) extends HttpFilters {
//  var filters = Seq(log, tls,gzip)
    var filters = Seq(log,gzip)
}
