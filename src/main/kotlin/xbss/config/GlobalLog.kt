package xbss.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xbss.MainAPP

object GlobalLog {
    private val logger: Logger = LoggerFactory.getLogger("global")
    fun writeInfoLog(info: String) {
        MainAPP.service.submit {
            logger.info(info)
        }
    }

    fun writeErrorLog(error: String) {
        MainAPP.service.submit {
            logger.error(error)
        }
    }
}