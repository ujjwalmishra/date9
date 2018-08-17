// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ujjwal/Desktop/date9/conf/routes
// @DATE:Thu Aug 09 19:43:35 CDT 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
