package com.thundermaps.apilib.android.api.responses.models

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
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gson
import java.lang.reflect.Type

@ExcludeFromJacocoGeneratedReport
data class FormField(
    @Expose val id: Int = 0,
    @Expose val label: String? = null,
    @Expose val key: String = "",
    @Expose val data: DataValue? = null,
    @Expose val editable: Boolean = false,
    @SerializedName("field_type") @Expose val fieldType: FieldType = FieldType.Unknown,
    @SerializedName("field_visibility") @Expose val fieldVisibility: String = "invisible",
    @SerializedName("form_order") @Expose val formOrder: Int = 0,
    @Expose val mandatory: Boolean? = null,
    @Expose val value: FormValue? = null,
    @Expose val visibility: String = ""
)

@ExcludeFromJacocoGeneratedReport
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
    RadioButton("RadioButton"),
    NumberedList("NumberedList"),
    Signature("Signature"),
    BulletedList("BulletedList"),
    DateAndTime("DateAndTime"),
    Unknown("Unknown")
}

@ExcludeFromJacocoGeneratedReport
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

@ExcludeFromJacocoGeneratedReport
open class DataValue(private val data: Any) {
    data class DataJsonObject(val value: JsonObject) : DataValue(value)
    data class DataString(val value: String) : DataValue(value)

    override fun equals(other: Any?): Boolean {
        return (data == (other as? DataValue)?.data)
    }
}

@ExcludeFromJacocoGeneratedReport
class DataValueDecode : JsonSerializer<DataValue>, JsonDeserializer<DataValue> {
    override fun serialize(
        src: DataValue,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = when (src) {
        is DataValue.DataString -> JsonPrimitive(src.value)
        is DataValue.DataJsonObject -> gson.toJsonTree(src.value)
        else -> JsonObject()
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DataValue? = when {
        json.isJsonObject -> DataValue.DataJsonObject(json.asJsonObject)
        json.isJsonPrimitive -> DataValue.DataString(json.asString)
        else -> null
    }
}

@ExcludeFromJacocoGeneratedReport
open class FormValue {
    @ExcludeFromJacocoGeneratedReport
    data class ValueInt(val value: Int = 0) : FormValue()

    @ExcludeFromJacocoGeneratedReport
    data class ValueString(val value: String = "") : FormValue()

    @ExcludeFromJacocoGeneratedReport
    data class ValueJsonArray(val value: JsonArray = JsonArray()) : FormValue()

    @ExcludeFromJacocoGeneratedReport
    data class ValueFormFieldImage(val images: List<FormFieldImage> = emptyList()) : FormValue()
}

@ExcludeFromJacocoGeneratedReport
class FormValueDecode : JsonSerializer<FormValue>, JsonDeserializer<FormValue> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): FormValue? {
        return when {
            json.isJsonPrimitive -> {
                try {
                    FormValue.ValueInt(json.asInt)
                } catch (exception: Exception) {
                    try {
                        FormValue.ValueString(json.asString)
                    } catch (exception: Exception) {
                        null
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
                null
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
        else -> JsonObject()
    }
}

@ExcludeFromJacocoGeneratedReport
data class OptionHolder(
    @SerializedName("multi_select") @Expose val isMultiSelect: Boolean,
    @Expose val options: List<Option>
)

@ExcludeFromJacocoGeneratedReport
data class Option(
    @Expose val label: String,
    @Expose val value: String,
    @Expose val enabled: Boolean,
    @SerializedName("multi_option_id") @Expose val multiOptionId: Long,
    @SerializedName("display_order") @Expose val displayOrder: Int,
    @SerializedName("is_default") @Expose val isDefault: Boolean = false
)
