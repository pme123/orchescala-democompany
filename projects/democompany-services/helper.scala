#!/usr/bin/env -S scala shebang
// DO NOT ADJUST. This file is replaced by `./helper.scala update`.

//> using dep democompany::democompany-orchescala-helper:0.0.0

import democompany.orchescala.helper.*


@main
def run(command: String, arguments: String*): Unit =
  CompanyDevHelper.run(command, arguments*)
