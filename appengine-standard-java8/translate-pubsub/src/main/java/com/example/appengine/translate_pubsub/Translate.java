package com.example.appengine.translate_pubsub;

import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.base.Strings;

public class Translate {
    /**
     * Translate the source text from source to target language.
     *
     * @param sourceText source text to be translated
     * @param sourceLang source language of the text
     * @param targetLang target language of translated text
     * @return source text translated into target language.
     */
    public static String translateText(
            String sourceText,
            String sourceLang,
            String targetLang) {
        if (Strings.isNullOrEmpty(sourceLang) || Strings.isNullOrEmpty(targetLang) || sourceLang.equals(targetLang)) {
            return sourceText;
        }
        com.google.cloud.translate.Translate translate = createTranslateService();
        TranslateOption srcLang = com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(sourceLang);
        TranslateOption tgtLang = com.google.cloud.translate.Translate.TranslateOption.targetLanguage(targetLang);

        Translation translation = translate.translate(sourceText, srcLang, tgtLang);
        return translation.getTranslatedText();
    }

    /**
     * Create Google Translate API Service.
     *
     * @return Google Translate Service
     */
    public static com.google.cloud.translate.Translate createTranslateService() {
        return TranslateOptions.newBuilder().build().getService();
    }
}
