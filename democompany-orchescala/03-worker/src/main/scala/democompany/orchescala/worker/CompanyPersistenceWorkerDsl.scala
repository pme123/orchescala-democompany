package democompany.orchescala.worker

import orchescala.persistence.postgres.{PostgresConfig, PostgresEntityStore}
import orchescala.worker.PersistenceWorkerDsl
import orchescala.worker.persistence.EntityStore

/** A worker with its own data - the store of the environment, the connection from `DB_*`
  * (`DB_URL`, `DB_USER`, `DB_PASSWORD`, …, see `PostgresConfig.fromEnv`). The project worker only
  * says what is stored or read (see [[PersistenceWorkerDsl]]).
  */
trait CompanyPersistenceWorkerDsl[In <: Product: InOutCodec, Out <: Product: InOutCodec]
    extends CompanyWorker[In, Out], PersistenceWorkerDsl[In, Out]:
  protected def entityStore: EntityStore = CompanyStore.store

object CompanyStore:
  lazy val store: EntityStore = PostgresEntityStore.app(PostgresConfig.fromEnv("DB"))
