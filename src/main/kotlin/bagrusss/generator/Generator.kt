package bagrusss.generator

import bagrusss.generator.model.KotlinPrimitiveModel
import bagrusss.generator.model.Model
import com.google.protobuf.compiler.PluginProtos
import com.squareup.kotlinpoet.*
import java.io.InputStream
import java.io.PrintStream

class Generator(private val input: InputStream,
                private val output: PrintStream,
                private val realmPath: String,
                private val realmPackage: String) {

    private companion object {

        @JvmField val packageName = "com.serenity.data_impl.realm.model"
        @JvmField var protoPackageName = ""
        @JvmField var protoFilePackage = ""
        @JvmField val prefix = "Realm"

    }

    private fun wtireClass() {

    }

    fun generate() {
        Logger.prepare()

        val response = PluginProtos.CodeGeneratorResponse.newBuilder()
        val request = PluginProtos.CodeGeneratorRequest.parseFrom(input)

        val primitives = listOf(Pair(INT, 0),
                                Pair(LONG, 0L),
                                Pair(DOUBLE, 0.0),
                                Pair(FLOAT, 0f),
                                Pair(BOOLEAN, false),
                                Pair(ClassName("kotlin", "String"), "\"\""))

        primitives.forEach {
            val primitiveModel: Model = KotlinPrimitiveModel(realmPackage, "Realm", it.first, it.second)
            val realmTypeFile = PluginProtos.CodeGeneratorResponse
                                            .File
                                            .newBuilder()
                                            .setName(primitiveModel.getFileName())
                                            .setContent(primitiveModel.getModelBody())
                                            .build()
            response.addFile(realmTypeFile)
        }



        request.protoFileList.forEach { protoFile ->
            protoPackageName = protoFile.options.javaPackage
            protoFilePackage = protoFile.`package`
            Logger.log("proto package ${protoFile.`package`}")
            protoFile.messageTypeList.forEach {
                if (it.hasOptions() /*&& it.options.hasExtension(SwiftDescriptor.swiftMessageOptions)*/) {
                    //if (it.hasOptions() && it.options.hasField(SwiftDescriptor.SwiftFileOptions.getDescriptor().fields.first { it.jsonName.contains("generate_realm_object", true) })) {
                    //parseCurrent(it, response)
                    Logger.log("proto full name ${it.name}")
                }
            }
        }
        response.build().writeTo(output)
    }
}