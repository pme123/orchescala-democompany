package democompany.orchescala.studio

import orchescala.orchstudio.{StudioConfig, StudioServer}

// sbt studio/run - then http://localhost:8880/site/ (orch-spec: /site/spec/, orch-ops: /site/ops/)
object StudioServerApp extends StudioServer:

  // the documentation comes with the app (`site/` on the classpath, see studioSiteSettings);
  // STUDIO_SITE_DIR=00-docs/site takes it from the folder instead, e.g. right after prepareDocs.
  // The folders of orch-spec and orch-ops - as in the preview of prepareDocs
  lazy val config: StudioConfig =
    StudioConfig(
      specDir = Some(os.pwd / "00-docs" / "testdata" / "spec"),
      opsDir = Some(os.pwd / "00-docs" / "testdata" / "ops")
    ).withEnv()

end StudioServerApp
