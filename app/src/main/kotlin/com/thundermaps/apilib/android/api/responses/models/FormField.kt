package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.InstanceCreator
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gson
import java.lang.reflect.Type
import org.json.JSONObject

data class FormField(
    @Expose val data: JSONObject = JSONObject(),
    @Expose val editable: Boolean = false,
    @SerializedName("field_type") @Expose val fieldType: FieldType = FieldType.Unknown,
    @SerializedName("field_visibility") @Expose val fieldVisibility: String = "invisible",
    @SerializedName("form_order") @Expose val formOrder: Int = 0,
    @Expose val id: Int = 0,
    @Expose val key: String = "",
    @Expose val mandatory: Boolean = false,
    @Expose val value: FormValue = FormValue.Unknown
)

enum class FieldType(val value: String) {
    Category("Category"),
    CheckBox("CheckBox"),
    DropDown("DropDown"),
    FileUpload("FileUpload"),
    FreeText("FreeText"),
    Image("Image"),
    LongTextBox("LongTextBox"),
    RelativePosition("RelativePosition"),
    ReportState("ReportState"),
    ReportViewers("ReportViewers"),
    RiskMatrix("RiskMatrix"),
    SectionBreak("SectionBreak"),
    ShortTextBox("ShortTextBox"),
    Unknown("Unknown")
}

class FieldTypeDecode : JsonDeserializer<FieldType>, JsonSerializer<FieldType> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): FieldType = try {
        FieldType.valueOf(json.asString)
    } catch (exception: Exception) {
        FieldType.Unknown
    }

    override fun serialize(
        src: FieldType,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = JsonPrimitive(src.value)
}

sealed class FormValue(val data: Any) {
    class ValueInt(val value: Int = 0) : FormValue(value)
    class ValueString(val value: String = "") : FormValue(value)
    class ValueJsonArray(val value: JsonArray = JsonArray()) : FormValue(value)
    class ValueFormFieldImage(val images: List<FormFieldImage> = emptyList()) : FormValue(images)
    object Unknown : FormValue(Unit)
}

class FormValueDecode : JsonSerializer<FormValue>, JsonDeserializer<FormValue>,
    InstanceCreator<FormValue> {

    override fun createInstance(type: Type?): FormValue {
        return FormValue.Unknown
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): FormValue {
        return when {
            json.isJsonPrimitive -> {
                try {
                    FormValue.ValueInt(json.asInt)
                } catch (exception: Exception) {
                    try {
                        FormValue.ValueString(json.asString)
                    } catch (exception: Exception) {
                        FormValue.Unknown
                    }
                }
            }
            json.isJsonArray -> {
                val array = json.asJsonArray
                if (array.size() == 0) {
                    FormValue.ValueJsonArray(json.asJsonArray)
                } else {
                    try {
                        FormValue.ValueFormFieldImage(
                            gson.fromJson(
                                json.asJsonArray,
                                object : TypeToken<List<FormFieldImage>>() {}.type
                            )
                        )
                    } catch (exception: Exception) {
                        print(exception)
                        FormValue.ValueJsonArray(json.asJsonArray)
                    }
                }
            }
            else -> {
                FormValue.Unknown
            }
        }
    }

    override fun serialize(
        src: FormValue,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = when (src) {
        is FormValue.ValueInt -> JsonPrimitive(src.value)
        is FormValue.ValueString -> JsonPrimitive(src.value)
        is FormValue.ValueFormFieldImage -> gson.toJsonTree(src.images)
        is FormValue.ValueJsonArray -> src.value
        is FormValue.Unknown -> JsonObject()
    }
}
