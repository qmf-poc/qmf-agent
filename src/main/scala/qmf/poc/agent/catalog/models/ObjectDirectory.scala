package qmf.poc.agent.catalog.models

import java.util.Date

class ObjectDirectory(val owner: String,
                      val name: String,
                      val `type`: String,
                      val subType: String,
                      val objectLevel: Int,
                      val restricted: String,
                      val model: String,
                      val created: String,
                      val modified: String,
                      val lastUsed: String,
                     )
