dataSource {
    pooled = true
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

// Settings are specified externally in the grails-app/conf/CustomConfig.groovy file
environments {
    development {
        dataSource {
        }
    }

    test {
        dataSource {
            dbCreate = "create"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }

    production {
        dataSource {
		}
    }
}
