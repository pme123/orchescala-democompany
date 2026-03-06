package democompany.orchescala.worker

import orchescala.worker.c7.{C7Context, C7Worker}
import orchescala.worker.c8.{C8Context, C8Worker}
import orchescala.worker.op.{OpContext, OpWorker}
import orchescala.worker.w4s.{W4SContext, W4SWorker}
import democompany.orchescala.worker.*

import scala.reflect.ClassTag

/**
 * Add here company specific stuff, to run the Workers.
 * You also define the implementation of the Worker here.
 */
trait CompanyWorker[In <: Product : InOutCodec, Out <: Product : InOutCodec]
  extends C7Worker[In, Out], C8Worker[In, Out], OpWorker[In, Out], W4SWorker[In, Out]:
  override lazy val logger: OrchescalaLogger = super[C7Worker].logger
  protected def c7Context: C7Context = CompanyEngineC7Context(CompanyRestApiC7Client())
  protected def c8Context: C8Context = CompanyEngineC8Context(CompanyRestApiC8Client())
  protected def operatonContext: OpContext = CompanyEngineOpContext(CompanyRestApiOpClient())
  protected def w4sContext: W4SContext = CompanyEngineW4SContext()

trait CompanyValidationWorkerDsl[
    In <: Product: InOutCodec
] extends CompanyWorker[In, NoOutput], ValidationWorkerDsl[In]

trait CompanyInitWorkerDsl[
    In <: Product: InOutCodec,
    Out <: Product: InOutCodec,
    InitIn <: Product: InOutCodec,
    InConfig <: Product: InOutCodec
] extends CompanyWorker[In, Out], InitWorkerDsl[In, Out, InitIn, InConfig]

trait CompanyCustomWorkerDsl[
    In <: Product: InOutCodec,
    Out <: Product: InOutCodec
] extends CompanyWorker[In, Out], CustomWorkerDsl[In, Out]

trait CompanyServiceWorkerDsl[
    In <: Product: InOutCodec,
    Out <: Product: InOutCodec,
    ServiceIn: InOutEncoder,
    ServiceOut: {InOutDecoder, ClassTag}
] extends CompanyWorker[In, Out], ServiceWorkerDsl[In, Out, ServiceIn, ServiceOut]
