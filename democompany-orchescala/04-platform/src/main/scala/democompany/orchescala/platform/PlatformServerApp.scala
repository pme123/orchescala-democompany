package democompany.orchescala.platform

import orchplatform.server.{PlatformConfig, PlatformServer}

// sbt platform/run - then http://localhost:8880/site/ (orch-spec: /site/spec/, orch-ops: /site/ops/)
object PlatformServerApp extends PlatformServer:

  // the documentation comes with the app (`site/` on the classpath, see platformSiteSettings);
  // PLATFORM_SITE_DIR=00-docs/site takes it from the folder instead, e.g. right after prepareDocs.
  // The folders of orch-spec and orch-ops - as in the preview of prepareDocs
  lazy val config: PlatformConfig =
    PlatformConfig(
      specDir = Some(os.pwd / "00-docs" / "testdata" / "spec"),
      opsDir = Some(os.pwd / "00-docs" / "testdata" / "ops")
    ).withEnv()

end PlatformServerApp
