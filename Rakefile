require 'bundler'
Bundler.setup()
require 'pry'

namespace :xdr do

  task :update => [:generate]

  task :generate do
    require "pathname"
    require "xdrgen"
    require 'fileutils'

    paths = Pathname.glob("xdr/*.x").sort
    compilation = Xdrgen::Compilation.new(
      paths,
      output_dir: "src/main/kotlin/org/tokend/wallet/xdr",
      namespace:  "org.tokend.wallet.xdr",
      language:   :kotlin
    )
    compilation.compile
  end
end
