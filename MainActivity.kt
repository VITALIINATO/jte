package com.example.a44sounds

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.gestures.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import java.util.*

// --- МОДЕЛЬ ДАННЫХ ДЛЯ НАВИГАЦИИ ---
data class NavHistoryItem(val screen: String, val phonemeId: String?)

// --- МОДЕЛЬ ДАННЫХ ЗВУКА ---
data class Phoneme(
    val id: String,
    val symbol: String,
    val letterLabel: String,
    val example: String,
    val transcription: String,
    val translation: String,
    val description: String,
    val imageUrl: Any,
    val category: String,
    val color: Color,
    val phonetic: String
)

val PHONEMES_LIST = listOf(
    Phoneme("i-long", "iː", "Ee", "sheep", "[  ʃ   iː   p  ]", "овца", "Долгий звук [ и ]. Губы слегка растянуты, как при улыбке.", "sheep", "monophthong", Color(0xFFFFD700), "ee"),
    Phoneme("i-short", "ɪ", "Ii", "ship", "[  ʃ   ɪ   p  ]", "корабль", "Краткий, ненапряженный звук [ и ].", "ship", "monophthong", Color(0xFFFFD700), "ih"),
    Phoneme("u-short", "ʊ", "Uu", "good", "[  ɡ   ʊ   d  ]", "хороший", "Краткий звук [ у ]. Губы слегка округлены.", "good", "monophthong", Color(0xFFFFD700), "uh"),
    Phoneme("u-long", "uː", "Uu", "shoot", "[  ʃ   uː   t  ]", "стрелять", "Долгий звук [ у ]. Губы сильно округлены и вытянуты вперед.", "shoot", "monophthong", Color(0xFFFFD700), "oo"),
    Phoneme("e", "e", "Ee", "left", "[  l   e   f   t  ]", "лево", "Краткий звук [ э ]. Рот приоткрыт шире, чем для [ ɪ ].", "left", "monophthong", Color(0xFFFFD700), "eh"),
    Phoneme("schwa", "ə", "Aa", "teacher", "[  ˈ   t   iː   tʃ   ə  ]", "учитель", "Нейтральный безударный звук. Самый частый звук в английском.", "teacher", "monophthong", Color(0xFFFFD700), "uh"),
    Phoneme("er-long", "ɜː", "Ur", "her", "[  h   ɜː  ]", "её", "Долгий звук, средний между [ о ] и [ э ].", "her", "monophthong", Color(0xFFFFD700), "er"),
    Phoneme("o-long", "ɔː", "Oo", "door", "[  d   ɔː  ]", "дверь", "Долгий звук [ о ]. Губы напряжены и округлены.", "door", "monophthong", Color(0xFFFFD700), "or"),
    Phoneme("ae", "æ", "Aa", "hat", "[  h   æ   t  ]", "шляпа", "Широкий звук [ э ]. Нижняя челюсть сильно опущена.", "hat", "monophthong", Color(0xFFFFD700), "aa"),
    Phoneme("v-short", "ʌ", "Uu", "up", "[  ʌ   p  ]", "вверх", "Краткий звук [ а ]. Рот приоткрыт, губы нейтральны.", "up", "monophthong", Color(0xFFFFD700), "uh"),
    Phoneme("a-long", "ɑː", "Aa", "far", "[  f   ɑː  ]", "далеко", "Глубокий долгий звук [ а ]. Рот широко открыт.", "far", "monophthong", Color(0xFFFFD700), "ah"),
    Phoneme("o-short", "ɒ", "Oo", "on", "[  ɒ   n  ]", "на", "Краткий звук [ о ]. Рот широко открыт, губы не округлены.", "on", "monophthong", Color(0xFFFFD700), "o"),
    Phoneme("ie", "ɪə", "Ee", "here", "[  h   ɪə  ]", "здесь", "Дифтонг. Переход от [ ɪ ] к нейтральному [ ə ].", "here", "diphthong", Color(0xFFFFA500), "ear"),
    Phoneme("ei", "eɪ", "Aa", "wait", "[  w   eɪ   t  ]", "ждать", "Дифтонг. Переход от [ e ] к [ ɪ ].", "wait", "diphthong", Color(0xFFFFA500), "ay"),
    Phoneme("ue", "ʊə", "Uu", "tour", "[  t   ʊə  ]", "тур", "Дифтонг. Переход от [ ʊ ] к нейтральному [ ə ].", "tour", "diphthong", Color(0xFFFFA500), "oor"),
    Phoneme("oi", "ɔɪ", "Oy", "boy", "[  b   ɔɪ  ]", "мальчик", "Дифтонг. Переход от [ ɔ ] к [ ɪ ].", "boy", "diphthong", Color(0xFFFFA500), "oy"),
    Phoneme("ou", "əʊ", "Oo", "show", "[  ʃ   əʊ  ]", "шоу", "Дифтонг. Переход от [ ə ] к [ ʊ ].", "show", "diphthong", Color(0xFFFFA500), "oh"),
    Phoneme("ea", "eə", "Ee", "hair", "[  h   eə  ]", "волосы", "Дифтонг. Переход от [ e ] к нейтральному [ ə ].", "hair", "diphthong", Color(0xFFFFA500), "air"),
    Phoneme("ai", "aɪ", "Ii", "my", "[  m   aɪ  ]", "мой", "Дифтонг. Переход от [ a ] к [ ɪ ].", "my", "diphthong", Color(0xFFFFA500), "eye"),
    Phoneme("au", "aʊ", "Ou", "cow", "[  k   aʊ  ]", "корова", "Дифтонг. Переход от [ a ] к [ ʊ ].", "cow", "diphthong", Color(0xFFFFA500), "ow"),
    Phoneme("p", "p", "Pp", "pen", "[  p   e   n  ]", "ручка", "близок русскому звуку [ п ], но энергичнее и с придыханием.", "pen", "plosive", Color(0xFF90EE90), "puh"),
    Phoneme("b", "b", "Bb", "bag", "[  b   æ   ɡ  ]", "сумка", "Звонкий звук [ б ]. Губы смыкаются и размыкаются.", "bag", "plosive", Color(0xFF90EE90), "buh"),
    Phoneme("t", "t", "Tt", "tea", "[  t   iː  ]", "чай", "Глухой звук [ т ]. Кончик языка на альвеолах.", "tea", "plosive", Color(0xFF90EE90), "tuh"),
    Phoneme("d", "d", "Dd", "dog", "[  d   ɒ   ɡ  ]", "собака", "Звонкий звук [ д ]. Кончик языка на альвеолах.", "dog", "plosive", Color(0xFF90EE90), "duh"),
    Phoneme("k", "k", "Kk", "car", "[  k   ɑː  ]", "машина", "Глухой звук [ к ]. Задняя часть языка касается неба.", "car", "plosive", Color(0xFF90EE90), "kuh"),
    Phoneme("g", "ɡ", "Gg", "go", "[  ɡ   əʊ  ]", "идти", "Звонкий звук [ г ]. Задняя часть языка касается неба.", "go", "plosive", Color(0xFF90EE90), "guh"),
    Phoneme("g-j", "dʒ", "Gg", "giraffe", "[  dʒ   ɪ   ˈ   r   ɑː   f  ]", "жираф", "Буква G может читаться как [dʒ] перед e, i, y.", "giraffe", "affricate", Color(0xFF22C55E), "juh"),
    Phoneme("ch", "tʃ", "ch", "cheese", "[  tʃ   iː   z  ]", "сыр", "Глухой звук [ ч ]. Начинается как [ t ], переходит в [ ʃ ].", "cheese", "affricate", Color(0xFF22C55E), "ch"),
    Phoneme("j", "dʒ", "Jj", "june", "[  dʒ   uː   n  ]", "июнь", "Звонкий звук [ дж ]. Начинается как [ d ], переходит в [ ʒ ].", "june", "affricate", Color(0xFF22C55E), "juh"),
    Phoneme("f", "f", "Ff", "fly", "[  f   l   aɪ  ]", "муха", "Глухой звук [ ф ]. Верхние зубы касаются нижней губы.", "fly", "fricative", Color(0xFF90EE90), "fuh"),
    Phoneme("v", "v", "Vv", "video", "[  ˈ   v   ɪ   d   ɪ   əʊ  ]", "видео", "Звонкий звук [ в ]. Верхние зубы касаются нижней губы.", "video", "fricative", Color(0xFF90EE90), "vuh"),
    Phoneme("th-unvoiced", "θ", "th", "think", "[  θ   ɪ   ŋ   k  ]", "думать", "Межзубный глухой звук. Кончик языка между зубами.", "think", "fricative", Color(0xFF90EE90), "th"),
    Phoneme("th-voiced", "ð", "th", "this", "[  ð   ɪ   s  ]", "это", "Межзубный звонкий звук. Кончик языка между зубами.", "this", "fricative", Color(0xFF90EE90), "the"),
    Phoneme("s", "s", "Ss", "sea", "[  s   iː  ]", "море", "Глухой звук [ с ]. Кончик языка за зубами.", "sea", "fricative", Color(0xFF90EE90), "suh"),
    Phoneme("s-z", "z", "Ss", "nose", "[  n   əʊ   z  ]", "нос", "Буква S может читаться как [z] между гласными.", "nose", "fricative", Color(0xFF22C55E), "zuh"),
    Phoneme("z", "z", "Zz", "zoo", "[  z   uː  ]", "зоопарк", "Звонкий звук [ з ]. Кончик языка за зубами.", "zoo", "fricative", Color(0xFF90EE90), "zuh"),
    Phoneme("sh", "ʃ", "sh", "shall", "[  ʃ   æ   l  ]", "должен", "Глухой звук [ ш ]. Кончик языка поднят к небу.", "shall", "fricative", Color(0xFF90EE90), "sh"),
    Phoneme("zh", "ʒ", "Ss", "television", "[  ˈ   t   e   l   ɪ   v   ɪ   ʒ   n  ]", "телевизор", "Звонкий звук [ ж ]. Кончик языка поднят к небу.", "television", "fricative", Color(0xFF90EE90), "zh"),
    Phoneme("h", "h", "Hh", "hat", "[  h   æ   t  ]", "шляпа", "Легкий выдох. Похож на русский [ х ], но слабее.", "hat", "fricative", Color(0xFF90EE90), "huh"),
    Phoneme("m", "m", "Mm", "man", "[  m   æ   n  ]", "человек", "Носовой звук [ м ]. Губы сомкнуты.", "man", "nasal", Color(0xFFD8BFD8), "muh"),
    Phoneme("n", "n", "Nn", "now", "[  n   aʊ  ]", "сейчас", "Носовой звук [ н ]. Кончик языка на альвеолах.", "now", "nasal", Color(0xFFD8BFD8), "nuh"),
    Phoneme("ng", "ŋ", "ng", "sing", "[  s   ɪ   ŋ  ]", "петь", "Заднеязычный носовой звук. Задняя часть языка у неба.", "sing", "nasal", Color(0xFFD8BFD8), "ng"),
    Phoneme("l", "l", "Ll", "love", "[  l   ʌ   v  ]", "любовь", "Звук [ л ]. Кончик языка на альвеолах.", "love", "liquid-glide", Color(0xFFF0E68C), "luh"),
    Phoneme("r", "r", "Rr", "red", "[  r   e   d  ]", "красный", "Звук [ р ]. Кончик языка поднят к небу, но не касается его.", "red", "liquid-glide", Color(0xFFF0E68C), "ruh"),
    Phoneme("w", "w", "Ww", "wet", "[  w   e   t  ]", "мокрый", "Звук [ уэ ]. Губы сильно округлены и размыкаются.", "wet", "liquid-glide", Color(0xFFF0E68C), "wuh"),
    Phoneme("y", "j", "Yy", "yes", "[  j   e   s  ]", "да", "Звук [ й ]. Средняя часть языка поднята к небу.", "yes", "liquid-glide", Color(0xFFF0E68C), "yuh"),

    // Additional Letters for Alphabet Screen
    Phoneme("letter-c", "s", "Cc", "city", "[  ˈ   s   ɪ   t   ɪ  ]", "город", "Буква C читается как [s] перед e, i, y.", "city", "fricative", Color(0xFF22C55E), "see"),
    Phoneme("letter-c-k", "k", "Cc", "cat", "[  k   æ   t  ]", "кот", "Буква C читается как [k] перед a, o, u.", "cat", "plosive", Color(0xFF22C55E), "kuh"),
    Phoneme("letter-q", "kw", "Qq", "queen", "[  k   w   iː   n  ]", "королева", "Буква Q обычно встречается в сочетании qu.", "queen", "plosive", Color(0xFF22C55E), "cue"),
    Phoneme("letter-x", "ks", "Xx", "box", "[  b   ɒ   k   s  ]", "коробка", "Буква X часто читается как [ks].", "box", "fricative", Color(0xFF22C55E), "ex"),
    Phoneme("letter-a-long", "eɪ", "Aa", "cake", "[  k   eɪ   k  ]", "торт", "Буква A читается как [eɪ] в открытом слоге.", "cake", "diphthong", Color(0xFF22C55E), "ay"),
    Phoneme("letter-a-short", "æ", "Aa", "apple", "[  ˈ   æ   p   l  ]", "яблоко", "Буква A читается как [æ] в закрытом слоге.", "apple", "monophthong", Color(0xFF22C55E), "aa"),
    Phoneme("letter-e-long", "iː", "Ee", "bee", "[  b   iː  ]", "пчела", "Буква E читается как [iː] в открытом слоге.", "bee", "monophthong", Color(0xFF22C55E), "ee"),
    Phoneme("letter-e-short", "e", "Ee", "egg", "[  e   ɡ  ]", "яйцо", "Буква E читается как [e] в закрытом слоге.", "egg", "monophthong", Color(0xFF22C55E), "eh"),
    Phoneme("letter-i-long", "aɪ", "Ii", "bike", "[  b   aɪ   k  ]", "велосипед", "Буква I читается как [aɪ] в открытом слоге.", "bike", "diphthong", Color(0xFF22C55E), "eye"),
    Phoneme("letter-i-short", "ɪ", "Ii", "ship", "[  ʃ   ɪ   p  ]", "корабль", "Буква I читается как [ɪ] в закрытом слоге.", "ship", "monophthong", Color(0xFF22C55E), "ih"),
    Phoneme("comb-air", "eə", "air", "air", "[  eə  ]", "воздух", "Сочетание air читается как [eə].", "air", "diphthong", Color(0xFF22C55E), "air"),
    Phoneme("letter-o-long", "əʊ", "Oo", "nose", "[  n   əʊ   z  ]", "нос", "Буква O читается как [əʊ] в открытом слоге.", "nose", "diphthong", Color(0xFF22C55E), "oh"),
    Phoneme("letter-o-short", "ɒ", "Oo", "orange", "[  ˈ   ɒ   r   ɪ   n   dʒ  ]", "апельсин", "Буква O читается как [ɒ] в закрытом слоге.", "orange", "monophthong", Color(0xFF22C55E), "o"),
    Phoneme("letter-u-long", "uː", "Uu", "blue", "[  b   l   uː  ]", "синий", "Буква U читается как [uː] в открытом слоге.", "blue", "monophthong", Color(0xFF22C55E), "oo"),
    Phoneme("letter-u-short", "ʌ", "Uu", "cup", "[  k   ʌ   p  ]", "чашка", "Буква U читается как [ʌ] в закрытом слоге.", "cup", "monophthong", Color(0xFF22C55E), "uh"),
    Phoneme("comb-sio", "ʃn", "sio", "mansion", "[  ˈ   m   æ   n   ʃ   n  ]", "особняк", "Сочетание sio часто читается как [ʃn].", "mansion", "fricative", Color(0xFF22C55E), "shun"),
    Phoneme("comb-ar", "ɑː", "ar", "car", "[  k   ɑː  ]", "машина", "Сочетание ar читается как долгий [ɑː].", "car", "monophthong", Color(0xFF22C55E), "ah"),
    Phoneme("comb-er", "ɜː", "er", "her", "[  h   ɜː  ]", "её", "Сочетание er читается как [ɜː].", "her", "monophthong", Color(0xFF22C55E), "er"),
    Phoneme("comb-or", "ɔː", "or", "door", "[  d   ɔː  ]", "дверь", "Сочетание or читается как [ɔː].", "door", "monophthong", Color(0xFF22C55E), "or"),
    Phoneme("comb-ir", "ɜː", "ir", "bird", "[  b   ɜː   d  ]", "птица", "Сочетание ir читается как [ɜː].", "bird", "monophthong", Color(0xFF22C55E), "er"),
    Phoneme("comb-oo", "uː", "oo", "moon", "[  m   uː   n  ]", "луна", "Сочетание oo часто читается как долгий [uː].", "moon", "monophthong", Color(0xFF22C55E), "oo"),
    Phoneme("comb-oi", "ɔɪ", "oi", "coin", "[  k   ɔɪ   n  ]", "монета", "Сочетание oi читается как [ɔɪ].", "coin", "diphthong", Color(0xFF22C55E), "oy"),
    Phoneme("comb-ou", "aʊ", "ou", "house", "[  h   aʊ   s  ]", "дом", "Сочетание ou часто читается как [aʊ].", "house", "diphthong", Color(0xFF22C55E), "ow"),
    Phoneme("comb-ear", "ɪə", "ear", "ear", "[  ɪə  ]", "ухо", "Сочетание ear может читаться как [ɪə].", "ear", "diphthong", Color(0xFF22C55E), "ear"),
    Phoneme("comb-ure", "ʊə", "ure", "pure", "[  p   j   ʊə  ]", "чистый", "Сочетание ure читается как [ʊə].", "pure", "diphthong", Color(0xFF22C55E), "oor")
)

val transcriptionMap = mapOf(
    // b
    "ba:" to "bah",
    "bɔ" to "bore",
    "bu:" to "boo",
    "bæ" to "ba",
    "bi:" to "bee",
    "a:" to "ah",
    "ɔ:" to "or",
    "u:" to "oo",
    "æ" to "aa",
    "i:" to "ee",
    "a:b" to "ahb",
    "ɔ:b" to "orb",
    "u:b" to "oob",
    "æb" to "ab",
    "i:b" to "eeb",
    // p
    "pa:" to "pah",
    "pɔ" to "pore",
    "pu:" to "poo",
    "pæ" to "pa",
    "pi:" to "pee",
    "a:p" to "ahp",
    "ɔp" to "op",
    "u:p" to "oop",
    "æp" to "ap",
    "i:p" to "eep",
    // f
    "fa:" to "fah",
    "fɔ" to "fore",
    "fu:" to "fu",
    "fæ" to "fa",
    "fi:" to "fee",
    "a:f" to "ahf",
    "ɔf" to "off",
    "u:f" to "oof",
    "æf" to "af",
    "i:f" to "eef",
    "ɔ" to "o",
    // v
    "va:" to "vah",
    "vɔ" to "vore",
    "vu:" to "voo",
    "væ" to "va",
    "vi:" to "vee",
    "a:v" to "ahv",
    "ɔv" to "ov",
    "u:v" to "oov",
    "æv" to "av",
    "i:v" to "eev",
    // m
    "ma:" to "mah",
    "mɔ:" to "more",
    "mu:" to "moo",
    "mæ" to "ma",
    "m:" to "mmm",
    "a:m" to "ahm",
    "ɔ:m" to "orm",
    "u:m" to "oom",
    "æm" to "am",
    "i:m" to "eem"
)

// --- ЛОКАЛИЗАЦИЯ И ПЕРЕВОДЫ ---
data class TranslationItem(val translation: String, val description: String)

val UKRAINIAN_PHONEME_TRANSLATIONS = mapOf(
    "i-long" to TranslationItem("вівця", "Довгий звук [ и ]. Губи злегка розтягнуті, як при посмішці."),
    "i-short" to TranslationItem("корабель", "Короткий, ненапружений звук [ и ]."),
    "u-short" to TranslationItem("хороший", "Короткий звук [ у ]. Губи злегка округлені."),
    "u-long" to TranslationItem("стріляти", "Довгий звук [ у ]. Губи сильно округлені та витягнуті вперед."),
    "e" to TranslationItem("ліво", "Короткий звук [ е ]. Рот привідкритий ширше, ніж для [ ɪ ]."),
    "schwa" to TranslationItem("вчитель", "Нейтральний ненаголошений звук. Найчастіший звук в англійській."),
    "er-long" to TranslationItem("її", "Довгий звук, середній між [ о ] та [ е ]."),
    "o-long" to TranslationItem("двері", "Довгий звук [ о ]. Губи напружені та округлені."),
    "ae" to TranslationItem("капелюх", "Широкий звук [ е ]. Нижня щелепа сильно опущена."),
    "v-short" to TranslationItem("вгору", "Короткий звук [ а ]. Рот привідкритий, губи нейтральні."),
    "a-long" to TranslationItem("далеко", "Глибокий довгий звук [ а ]. Рот широко відкритий."),
    "o-short" to TranslationItem("на", "Короткий звук [ о ]. Рот широко відкритий, губи не округлені."),
    "ie" to TranslationItem("тут", "Дифтонг. Перехід від [ ɪ ] до нейтрального [ ə ]."),
    "ei" to TranslationItem("чекати", "Дифтонг. Перехід від [ e ] до [ ɪ ]."),
    "ue" to TranslationItem("тур", "Дифтонг. Перехід від [ ʊ ] до нейтрального [ ə ]."),
    "oi" to TranslationItem("хлопчик", "Дифтонг. Перехід від [ ɔ ] до [ ɪ ]."),
    "ou" to TranslationItem("шоу", "Дифтонг. Перехід від [ ə ] до [ ʊ ]."),
    "ea" to TranslationItem("волосся", "Дифтонг. Перехід від [ e ] до нейтрального [ ə ]."),
    "ai" to TranslationItem("мій", "Дифтонг. Перехід від [ a ] до [ ɪ ]."),
    "au" to TranslationItem("корова", "Дифтонг. Перехід від [ a ] до [ ʊ ]."),
    "p" to TranslationItem("ручка", "близький до українського звуку [ п ], але енергійніший і з придихом."),
    "b" to TranslationItem("сумка", "Дзвінкий звук [ б ]. Губи змикаються і розмикаються."),
    "t" to TranslationItem("чай", "Глухий звук [ т ]. Кінчик язика на альвеолах."),
    "d" to TranslationItem("собака", "Дзвінкий звук [ д ]. Кінчик язика на альвеолах."),
    "k" to TranslationItem("машина", "Глухий звук [ к ]. Задня частина язика торкається піднебіння."),
    "g" to TranslationItem("йти", "Дзвінкий звук [ г ]. Задня частина язика торкається піднебіння."),
    "g-j" to TranslationItem("жираф", "Буква G може читатися як [dʒ] перед e, i, y."),
    "ch" to TranslationItem("сир", "Глухий звук [ ч ]. Починається як [ t ], переходить в [ ʃ ]."),
    "j" to TranslationItem("червень", "Дзвінкий звук [ дж ]. Починається як [ d ], переходить в [ ʒ ]."),
    "f" to TranslationItem("муха", "Глухий звук [ ф ]. Верхні зуби торкаються нижньої губи."),
    "v" to TranslationItem("відео", "Дзвінкий звук [ в ]. Upper зуби торкаються нижньої губи."),
    "th-unvoiced" to TranslationItem("думати", "Міжзубний глухий звук. Кінчик язика між зубами."),
    "th-voiced" to TranslationItem("це", "Міжзубний дзвінкий звук. Кінчик язика між зубами."),
    "s" to TranslationItem("море", "Глухий звук [ с ]. Кінчик язика за зубами."),
    "s-z" to TranslationItem("ніс", "Буква S може читатися як [z] між голосними."),
    "z" to TranslationItem("зоопарк", "Дзвінкий звук [ з ]. Кінчик язика за зубами."),
    "sh" to TranslationItem("повинен", "Глухий звук [ ш ]. Кінчик язика піднятий до піднебіння."),
    "zh" to TranslationItem("телевізор", "Дзвінкий звук [ ж ]. Кінчик язика піднятий до піднебіння."),
    "h" to TranslationItem("капелюх", "Легкий видих. Схожий на український [ х ], але слабший."),
    "m" to TranslationItem("людина", "Носовий звук [ м ]. Губи зімкнуті."),
    "n" to TranslationItem("зараз", "Носовий звук [ н ]. Кінчик язика на альвеолах."),
    "ng" to TranslationItem("співати", "Задньоязиковий носовий звук. Задня частина язика біля піднебіння."),
    "l" to TranslationItem("любов", "Звук [ л ]. Кінчик язика на альвеолах."),
    "r" to TranslationItem("червоний", "Звук [ р ]. Кінчик язика піднятий до піднебіння, але не торкається його."),
    "w" to TranslationItem("мокрий", "Звук [ уэ ]. Губи сильно округлені та розмикаються."),
    "y" to TranslationItem("так", "Звук [ й ]. Середня частина язика піднята до піднебіння."),
    "letter-c" to TranslationItem("місто", "Буква C читається як [s] перед e, i, y."),
    "letter-c-k" to TranslationItem("кіт", "Буква C читається як [k] перед a, o, u."),
    "letter-q" to TranslationItem("королева", "Буква Q зазвичай зустрічається в поєднанні qu."),
    "letter-x" to TranslationItem("коробка", "Буква X часто читається як [ks]."),
    "letter-a-long" to TranslationItem("торт", "Буква A читається як [eɪ] у відкритому складі."),
    "letter-a-short" to TranslationItem("яблуко", "Буква A читається як [æ] у закритому складі."),
    "letter-e-long" to TranslationItem("бджола", "Буква E читається як [iː] у відкритому складі."),
    "letter-e-short" to TranslationItem("яйце", "Буква E читається як [e] у закритому складі."),
    "letter-i-long" to TranslationItem("велосипед", "Буква I читається як [aɪ] у відкритому складі."),
    "letter-i-short" to TranslationItem("корабель", "Буква I читається як [ɪ] у закритому складі."),
    "letter-o-long" to TranslationItem("ніс", "Буква O читається як [əʊ] у відкритому складі."),
    "letter-o-short" to TranslationItem("апельсин", "Буква O читається як [ɒ] у закритому складі."),
    "letter-u-long" to TranslationItem("синій", "Буква U читається як [uː] у відкритому складі."),
    "letter-u-short" to TranslationItem("чашка", "Буква U читається як [ʌ] у закритому складі."),
    "comb-sio" to TranslationItem("особняк", "Поєднання sio часто читається як [ʃn]."),
    "comb-ar" to TranslationItem("машина", "Поєднання ar читається як довгий [ɑː]."),
    "comb-er" to TranslationItem("її", "Поєднання er читається як [ɜː]."),
    "comb-or" to TranslationItem("двері", "Поєднання or читається як [ɔː]."),
    "comb-ir" to TranslationItem("птах", "Поєднання ir читається як [ɜː]."),
    "comb-oo" to TranslationItem("місяць", "Поєднання oo часто читається як довгий [uː]."),
    "comb-oi" to TranslationItem("монета", "Поєднання oi читається як [ɔɪ]."),
    "comb-ou" to TranslationItem("будинок", "Поєднання ou часто читається як [aʊ]."),
    "comb-ear" to TranslationItem("вухо", "Поєднання ear може читатися як [ɪə]."),
    "comb-ure" to TranslationItem("чистий", "Поєднання ure читається як [ʊə]."),
    "comb-air" to TranslationItem("повітря", "Поєднання air читається как [eə].")
)

fun getLocalizedText(id: String, defaultVal: String, lang: String, isDescription: Boolean = false): String {
    if (lang == "ua") {
        val trans = UKRAINIAN_PHONEME_TRANSLATIONS[id]
        if (trans != null) {
            return if (isDescription) trans.description else trans.translation
        }
    }
    return defaultVal
}

fun t(key: String, lang: String): String {
    val ruMap = mapOf(
        "step_by_step" to "Авторская программа",
        "intro_desc" to "Принципы авторской программы основаны на постепенном переходе от простого к сложному. Мы начинаем с основ алфавита, переходим к изучению каждого из 44 звуков, осваиваем правила чтения и закрепляем всё это в практических уроках.",
        "do_not_show_again" to "Не показывать снова",
        "start_learning" to "Начать обучение",
        "home_tab" to "Главная",
        "sounds_tab" to "База звуков",
        "letters_tab" to "База букв",
        "practice_tab" to "Уроки",
        "lessons" to "Уроки",
        "consonants" to "Согласные звуки",
        "vowels" to "Гласные звуки",
        "consonants_caps" to "СОГЛАСНЫЕ",
        "vowels_caps" to "ГЛАСНЫЕ",
        "dark_theme" to "Темная тема",
        "reset_progress" to "Сбросить прогресс",
        "reset_all" to "Сбросить всё",
        "settings" to "Настройки",
        "close" to "Закрыть",
        "select_portion" to "Выберите порцию для обучения",
        "portion_1" to "Порция 1",
        "portion_1_desc" to "Базовые согласные и гласные",
        "portion_2" to "Порция 2",
        "portion_2_desc" to "Продвинутые звуки и практика",
        "pronounce_syllabes" to "ПРОИЗНЕСИТЕ ДАННЫЕ СЛОГИ",
        "lesson_learned" to "Урок усвоен?",
        "hard" to "Сложно",
        "normal" to "Нормально",
        "learned" to "Урок усвоен",
        "settings_lang" to "Язык приложения",
        "button_letter" to "Буква",
        "button_sound" to "звук",
        "consonants_short" to "СОГЛАСНЫЕ",
        "vowels_short" to "ГЛАСНЫЕ",
        "start_desc" to "БАЗА ДЛЯ СТАРТА В АНГЛИЙСКИЙ",
        "start_sub" to "(навык читать, писать, говорить, понимать)",
        "menu_letters" to "26 букв алфавита",
        "menu_letters_sub" to "Названия букв",
        "menu_sounds" to "44 звука",
        "menu_sounds_sub" to "Изучение фонетики",
        "menu_rules" to "Чтение букв",
        "menu_rules_sub" to "Правила чтения",
        "menu_practice" to "Практика",
        "menu_practice_sub" to "Закрепление знаний в уроках",
        "confirm_lesson" to "Подтвердите выполнение урока",
        "repeat_lesson" to "Назад (повторить)",
        "sound_intro" to "Знакомство со звуком",
        "sound_syllables" to "Слоги со звуком",
        "sound_words" to "Слова со звуком",
        "step_word" to "Шаг",
        "of_total" to "из",
        "next_btn" to "Далее",
        "back_btn" to "Назад",
        "step_by_step_colon" to "Пошагово:",
        "learning_progress" to "ПРОГРЕСС ОБУЧЕНИЯ"
    )
    val uaMap = mapOf(
        "step_by_step" to "Авторська програма",
        "intro_desc" to "Принципи авторської програми засновані на поступовому переході від простого до складного. Ми починаємо з основ алфавіту, переходимо до вивчення кожного з 44 звуків, освоюємо правила читання і закріплюємо все це в практичних уроках.",
        "do_not_show_again" to "Не показувати знову",
        "start_learning" to "Почати навчання",
        "home_tab" to "Головна",
        "sounds_tab" to "База звуків",
        "letters_tab" to "База букв",
        "practice_tab" to "Уроки",
        "lessons" to "Уроки",
        "consonants" to "Приголосні звуки",
        "vowels" to "Голосні звуки",
        "consonants_caps" to "ПРИГОЛОСНІ",
        "vowels_caps" to "ГОЛОСНІ",
        "dark_theme" to "Темна тема",
        "reset_progress" to "Скинути прогрес",
        "reset_all" to "Скинути все",
        "settings" to "Налаштування",
        "close" to "Закрити",
        "select_portion" to "Оберіть порцію для навчання",
        "portion_1" to "Порція 1",
        "portion_1_desc" to "Базові приголосні та голосні",
        "portion_2" to "Порція 2",
        "portion_2_desc" to "Просунуті звуки та практика",
        "pronounce_syllabes" to "ВИМОВІТЬ ДАНІ СКЛАДИ",
        "lesson_learned" to "Урок засвоєно?",
        "hard" to "Складно",
        "normal" to "Нормально",
        "learned" to "Урок засвоєно",
        "settings_lang" to "Мова додатка",
        "button_letter" to "Буква",
        "button_sound" to "звук",
        "consonants_short" to "ПРИГОЛОСНІ",
        "vowels_short" to "ГОЛОСНІ",
        "start_desc" to "БАЗА ДЛЯ СТАРТУ В АНГЛІЙСЬКУ",
        "start_sub" to "(навичка читати, писати, говорити, розуміти)",
        "menu_letters" to "26 букв алфавіту",
        "menu_letters_sub" to "Назви букв",
        "menu_sounds" to "44 звуки",
        "menu_sounds_sub" to "Вивчення фонетики",
        "menu_rules" to "Читання букв",
        "menu_rules_sub" to "Правила читання",
        "menu_practice" to "Практика",
        "menu_practice_sub" to "Закріплення знань в уроках",
        "confirm_lesson" to "Підтвердьте виконання уроку",
        "repeat_lesson" to "Назад (повторити)",
        "sound_intro" to "Знайомство зі звуком",
        "sound_syllables" to "Склади зі звуком",
        "sound_words" to "Слова зі звуком",
        "step_word" to "Крок",
        "of_total" to "з",
        "next_btn" to "Далі",
        "back_btn" to "Назад",
        "step_by_step_colon" to "Покроково:",
        "learning_progress" to "ПРОГРЕС НАВЧАННЯ"
    )
    return if (lang == "ua") uaMap[key] ?: (ruMap[key] ?: key) else ruMap[key] ?: key
}

fun getTtsPronounceableText(text: String): String {
    val cleanTranscription = text.replace("[", "").replace("]", "").trim()
    val mapped = transcriptionMap[cleanTranscription]
    val utterString = mapped ?: cleanTranscription

    return utterString.lowercase().trim()
}

// --- ГЛАВНАЯ АКТИВНОСТЬ ---
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val isTtsReady = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val sharedPrefs = getSharedPreferences("app_progress", MODE_PRIVATE)
        val initialLessons = sharedPrefs.getStringSet("completed_lessons", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val skipWelcome = sharedPrefs.getBoolean("skip_welcome", false)
        val isDark = sharedPrefs.getBoolean("is_dark_mode", true)
        val hasLanguage = sharedPrefs.contains("language")
        val initialLang = sharedPrefs.getString("language", "ru") ?: "ru"

        setContent {
            var isDarkModeState by remember { mutableStateOf(isDark) }
            var showSettingsDialog by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf(if (!hasLanguage) "language_select" else if (skipWelcome) "menu" else "welcome") }
            var activeTab by remember { mutableStateOf("home") }
            var selectedPhonemeId by remember { mutableStateOf<String?>(null) }
            val navHistory = remember { mutableStateListOf<NavHistoryItem>() }
            val completedLessonsState = remember { mutableStateOf(initialLessons) }
            var selectedLesson by remember { mutableIntStateOf(0) }
            var lessonStep by remember { mutableIntStateOf(0) }
            var skipWelcomeState by remember { mutableStateOf(skipWelcome) }
            var languageState by remember { mutableStateOf(initialLang) }

            // Persistence observer
            LaunchedEffect(completedLessonsState.value) {
                sharedPrefs.edit().putStringSet("completed_lessons", completedLessonsState.value.map { it.toString() }.toSet()).apply()
            }

            LaunchedEffect(skipWelcomeState) {
                sharedPrefs.edit().putBoolean("skip_welcome", skipWelcomeState).apply()
            }

            LaunchedEffect(isDarkModeState) {
                sharedPrefs.edit().putBoolean("is_dark_mode", isDarkModeState).apply()
            }

            LaunchedEffect(languageState) {
                sharedPrefs.edit().putString("language", languageState).apply()
            }

            MaterialTheme(
                colorScheme = if (isDarkModeState) {
                    darkColorScheme(
                        primary = Color(0xFF22C55E),
                        background = Color(0xFF1A1A1A),
                        surface = Color(0xFF121212),
                        onBackground = Color.White,
                        onSurface = Color.White
                    )
                } else {
                    lightColorScheme(
                        primary = Color(0xFF16A34A),
                        background = Color(0xFFF9FAFB),
                        surface = Color.White,
                        onBackground = Color(0xFF111827),
                        onSurface = Color(0xFF111827)
                    )
                }
            ) {
                Scaffold(
                    containerColor = Color(0xFF1A1A1A),
                    bottomBar = {
                        if (currentScreen != "welcome" && currentScreen != "language_select") {
                            Surface(
                                color = Color(0xFF121212),
                                modifier = Modifier.height(64.dp).fillMaxWidth(),
                                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val tabs = listOf(
                                        Triple("home", "Главная", Icons.Default.Home),
                                        Triple("practice", "Практика", Icons.Default.PlayArrow),
                                        Triple("sounds", "Звуки", Icons.Default.List),
                                        Triple("letters", "Буквы", Icons.Default.Info)
                                    )

                                    tabs.forEach { tab: Triple<String, String, ImageVector> ->
                                        val id = tab.first
                                        val label = t(id + "_tab", languageState)
                                        val icon = tab.third
                                        val isActive = activeTab == id
                                        val color = when(id) {
                                            "sounds" -> if(isActive) Color(0xFF22C55E) else Color.White.copy(alpha = 0.4f)
                                            "letters" -> if(isActive) Color(0xFF22C55E) else Color.White.copy(alpha = 0.4f)
                                            "practice" -> if(isActive) Color(0xFF22C55E) else Color.White.copy(alpha = 0.4f)
                                            else -> if(isActive) Color.White else Color.White.copy(alpha = 0.4f)
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clickable {
                                                    activeTab = id
                                                    navHistory.clear()
                                                    if (id == "home") currentScreen = "menu"
                                                    if (id == "sounds") currentScreen = "phonemes"
                                                    if (id == "letters") currentScreen = "alphabet"
                                                    if (id == "practice") currentScreen = "practice"
                                                }
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = label,
                                                tint = color,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(label, fontSize = 10.sp, color = color)
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFF1A1A1A))) {
                        when (currentScreen) {
                            "language_select" -> LanguageSelectScreen(
                                onSelect = { lang ->
                                    languageState = lang
                                    sharedPrefs.edit().putString("language", lang).apply()
                                    currentScreen = "welcome"
                                }
                            )
                            "welcome" -> WelcomeScreen(
                                isDarkMode = isDarkModeState,
                                skipWelcome = skipWelcomeState,
                                language = languageState,
                                onSkipChange = { skipWelcomeState = it },
                                onStart = {
                                    currentScreen = "menu"
                                    navHistory.clear()
                                }
                            )
                            "menu" -> MainMenuScreen(
                                isDarkMode = isDarkModeState,
                                language = languageState,
                                onOpenSettings = { showSettingsDialog = true },
                                onNavigate = { screen: String ->
                                    currentScreen = screen
                                    navHistory.clear()
                                    if (screen == "phonemes") activeTab = "sounds"
                                    if (screen == "alphabet") activeTab = "letters"
                                    if (screen == "practice") activeTab = "practice"
                                    if (screen == "alphabet_grid") activeTab = "home"
                                }
                            )
                            "alphabet_grid" -> AlphabetGridScreen(
                                isDarkMode = isDarkModeState,
                                onBack = {
                                    currentScreen = "menu"
                                    activeTab = "home"
                                    navHistory.clear()
                                },
                                onPlayLetter = { letter: String -> if (isTtsReady.value) tts?.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null) }
                            )
                            "alphabet" -> AlphabetScreen(
                                isDarkMode = isDarkModeState,
                                initialPhonemeId = selectedPhonemeId,
                                onBack = {
                                    if (navHistory.isNotEmpty()) {
                                        val last = navHistory.removeAt(navHistory.size - 1)
                                        currentScreen = last.screen
                                        selectedPhonemeId = last.phonemeId

                                        if (last.screen == "phonemes") activeTab = "sounds"
                                        if (last.screen == "alphabet") activeTab = "letters"
                                        if (last.screen == "practice") activeTab = "practice"
                                        if (last.screen == "menu" || last.screen == "alphabet_grid") activeTab = "home"
                                    } else {
                                        currentScreen = "menu"
                                        activeTab = "home"
                                    }
                                },
                                onPlayWord = { text: String -> playWordSound(text) },
                                onPlaySound = { id: String, isLetter: Boolean -> playLocalSound(id, isLetter) },
                                onNavigateToSounds = { symbol: String, currentPhonemeId: String ->
                                    val target = PHONEMES_LIST.find { p: Phoneme ->
                                        p.symbol == symbol && !p.id.startsWith("letter-") && !p.id.startsWith("comb-")
                                    } ?: PHONEMES_LIST.find { p: Phoneme -> p.symbol == symbol }

                                    if (target != null) {
                                        navHistory.add(NavHistoryItem(currentScreen, currentPhonemeId))
                                        selectedPhonemeId = target.id
                                        currentScreen = "phonemes"
                                        activeTab = "sounds"
                                    }
                                },
                                onPhonemeChange = { id: String -> selectedPhonemeId = id },
                                language = languageState
                            )
                            "phonemes" -> PhonemicApp(
                                isDarkMode = isDarkModeState,
                                initialPhonemeId = selectedPhonemeId,
                                onBack = {
                                    if (navHistory.isNotEmpty()) {
                                        val last = navHistory.removeAt(navHistory.size - 1)
                                        currentScreen = last.screen
                                        selectedPhonemeId = last.phonemeId

                                        if (last.screen == "phonemes") activeTab = "sounds"
                                        if (last.screen == "alphabet") activeTab = "letters"
                                        if (last.screen == "practice") activeTab = "practice"
                                        if (last.screen == "menu" || last.screen == "alphabet_grid") activeTab = "home"
                                    } else {
                                        currentScreen = "menu"
                                        activeTab = "home"
                                        selectedPhonemeId = null
                                    }
                                },
                                onPlayWord = { text: String -> playWordSound(text) },
                                onPlaySound = { id: String -> playLocalSound(id) },
                                onNavigateToPhoneme = { symbol: String ->
                                    val target = PHONEMES_LIST.find { p: Phoneme ->
                                        p.symbol == symbol && !p.id.startsWith("letter-") && !p.id.startsWith("comb-")
                                    } ?: PHONEMES_LIST.find { p: Phoneme -> p.symbol == symbol }

                                    if (target != null) {
                                        navHistory.add(NavHistoryItem(currentScreen, selectedPhonemeId))
                                        selectedPhonemeId = target.id
                                    }
                                },
                                onPhonemeChange = { id: String -> selectedPhonemeId = id },
                                language = languageState
                            )
                            "practice" -> PracticeScreen(
                                isDarkMode = isDarkModeState,
                                completedLessons = completedLessonsState,
                                selectedLesson = selectedLesson,
                                onLessonChange = { lesson: Int -> selectedLesson = lesson },
                                lessonStep = lessonStep,
                                onStepChange = { step: Int -> lessonStep = step },
                                onBack = {
                                    currentScreen = "menu"
                                    activeTab = "home"
                                    navHistory.clear()
                                },
                                onPlaySound = { id: String -> playLocalSound(id) },
                                onPlayWord = { text: String -> playWordSound(text) },
                                onPhonemeClick = { symbol: String, currentPhonemeId: String ->
                                    val target = PHONEMES_LIST.find { it.id == symbol } ?:
                                    PHONEMES_LIST.find { p: Phoneme -> p.symbol == symbol && !p.id.startsWith("letter-") && !p.id.startsWith("comb-") } ?:
                                    PHONEMES_LIST.find { p: Phoneme -> p.symbol == symbol }
                                    if (target != null) {
                                        navHistory.add(NavHistoryItem(currentScreen, currentPhonemeId))
                                        selectedPhonemeId = target.id
                                        currentScreen = if (target.id.startsWith("letter-") || target.id.startsWith("comb-")) "alphabet" else "phonemes"
                                        activeTab = if (currentScreen == "alphabet") "letters" else "sounds"
                                    }
                                },
                                onLessonComplete = { lesson: Int ->
                                    val newList = completedLessonsState.value.toMutableSet()
                                    newList.add(lesson)
                                    completedLessonsState.value = newList
                                },
                                language = languageState
                            )
                        }

                        if (showSettingsDialog) {
                            SettingsDialog(
                                isDarkMode = isDarkModeState,
                                onDarkModeChange = { isDarkModeState = it },
                                language = languageState,
                                onLanguageChange = { languageState = it },
                                onDismiss = { showSettingsDialog = false },
                                onResetProgress = {
                                    completedLessonsState.value = emptySet()
                                    showSettingsDialog = false
                                },
                                onResetSettings = {
                                    completedLessonsState.value = emptySet()
                                    skipWelcomeState = false
                                    isDarkModeState = true
                                    sharedPrefs.edit().remove("language").apply()
                                    currentScreen = "language_select"
                                    showSettingsDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun playLocalSound(id: String, isLetter: Boolean = false) {
        try {
            var resName = id.replace("-", "_")
            if (isLetter) resName = resName + resName
            val resourceId = resources.getIdentifier(resName, "raw", packageName)
            if (resourceId != 0) {
                MediaPlayer.create(this, resourceId).start()
            }
        } catch (e: Exception) { }
    }

    private fun playWordSound(text: String) {
        try {
            val clean = text.replace("[", "").replace("]", "").replace(":", "").replace("ː", "").trim().lowercase()
            val resId = resources.getIdentifier(clean, "raw", packageName)
            if (resId != 0) {
                MediaPlayer.create(this, resId).start()
            } else {
                if (isTtsReady.value) tts?.speak(getTtsPronounceableText(text), TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } catch (e: Exception) {
            try {
                if (isTtsReady.value) tts?.speak(getTtsPronounceableText(text), TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (ex: Exception) { }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady.value = true
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun LanguageSelectScreen(onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Color(0xFF22C55E).copy(alpha = 0.1f), CircleShape)
                .border(BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.3f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF22C55E),
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Choose Language",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Выберите язык объяснений для обучения\nОберіть мову пояснень для навчання",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onSelect("ru") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Русский", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Язык объяснений: Русский", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
            
            Button(
                onClick = { onSelect("ua") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    contentColor = Color(0xFF22C55E)
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Українська", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                    Text("Мова пояснень: Українська", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(isDarkMode: Boolean, skipWelcome: Boolean, language: String, onSkipChange: (Boolean) -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = RoundedCornerShape(48.dp),
            color = Color(0xFF22C55E).copy(alpha = 0.2f),
            border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(48.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            t("step_by_step", language),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            t("intro_desc", language),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.clickable { onSkipChange(!skipWelcome) }.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = skipWelcome,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22C55E))
            )
            Spacer(Modifier.width(8.dp))
            Text(t("do_not_show_again", language), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
        ) {
            Text(t("start_learning", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun TranscriptionText(
    transcription: String,
    currentSymbol: String,
    onSymbolClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp
) {
    val allSymbols = remember { PHONEMES_LIST.map { p: Phoneme -> p.symbol }.distinct().sortedByDescending { symbol: String -> symbol.length } }

    val annotatedString = buildAnnotatedString {
        var remaining = transcription
        while (remaining.isNotEmpty()) {
            val match = allSymbols.find { s: String -> remaining.startsWith(s) }
            if (match != null) {
                pushStringAnnotation(tag = "PHONEME", annotation = match)
                withStyle(style = SpanStyle(
                    color = if (match == currentSymbol) Color(0xFF22C55E) else Color.White.copy(alpha = 0.6f),
                    fontWeight = if (match == currentSymbol) FontWeight.Bold else FontWeight.Normal
                )) {
                    append(match)
                }
                pop()
                remaining = remaining.substring(match.length)
            } else {
                val char = remaining[0]
                withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.3f))) {
                    append(if (char == ' ') ' ' else char)
                }
                remaining = remaining.substring(1)
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(fontSize = fontSize, textAlign = TextAlign.Center),
        modifier = modifier,
        onClick = { offset: Int ->
            annotatedString.getStringAnnotations(tag = "PHONEME", start = offset, end = offset)
                .firstOrNull()?.let { annotation: AnnotatedString.Range<String> ->
                    onSymbolClick(annotation.item)
                }
        }
    )
}

@Composable
fun PracticeScreen(
    isDarkMode: Boolean,
    completedLessons: MutableState<Set<Int>>,
    selectedLesson: Int,
    onLessonChange: (Int) -> Unit,
    lessonStep: Int,
    onStepChange: (Int) -> Unit,
    onBack: () -> Unit,
    onPlaySound: (String) -> Unit,
    onPlayWord: (String) -> Unit,
    onPhonemeClick: (String, String) -> Unit,
    onLessonComplete: (Int) -> Unit,
    language: String
) {
    val lessons = (1..15).toList()
    val progress = (completedLessons.value.size.toFloat() / 15f)
    
    val lessonConfig = remember {
        mapOf(
            1 to listOf("b"),
            2 to listOf("p"),
            3 to listOf("v"),
            4 to listOf("f"),
            5 to listOf("m"),
            6 to listOf("s", "z"),
            7 to listOf("g", "k"),
            8 to listOf("t", "d", "n", "l"),
            9 to listOf("h", "w", "r", "y", "j"),
            10 to listOf("ch", "sh", "zh", "th-unvoiced", "th-voiced", "ng"),
            11 to listOf("ei", "ae", "i-long", "e"),
            12 to listOf("ai", "i-short", "ou", "o-short"),
            13 to listOf("u-long", "v-short", "a-long", "schwa"),
            14 to listOf("o-long", "er-long", "u-short", "oi", "au"),
            15 to listOf("ie", "ue", "ea")
        )
    }

    val lipAnimations = remember(language) {
        if (language == "ua") {
            mapOf(
                "b" to listOf("Зімкніть губи", "розсуньте їх з потоком повітря", "увімкніть голос"),
                "p" to listOf("Зімкніть губи", "розсуньте їх з видихом", "вимкніть голос"),
                "v" to listOf("Притисніть верхні зуби до нижньої губи", "випустіть струмінь повітря", "увімкніть голос"),
                "f" to listOf("Притисніть верхні зуби до нижньої губи", "випустіть струмінь повітря", "вимкніть голос"),
                "m" to listOf("Закройте рот", "видавіть звук з носа", "увімкніть голос")
            )
        } else {
            mapOf(
                "b" to listOf("Сомкните губы", "раздвиньте их с потоком воздуха", "включите голос"),
                "p" to listOf("Сомкните губы", "раздвиньте их с выдохом", "выключите голос"),
                "v" to listOf("Прижмите верхние зубы к нижней губе", "выпустите струю воздуха", "включите голос"),
                "f" to listOf("Прижмите верхние зубы к нижней губе", "выпустите струю воздуха", "выключите голос"),
                "m" to listOf("Закройте рот", "выдавите звук из носа", "включите голос")
            )
        }
    }

    val syllableExamples = remember {
        mapOf(
            "b" to listOf("[ba:] – [bɔ] - [bu:] -[bæ] – [bi:] ", "[a:b] – [ɔ:b] - [u:b] -[æb] – [i:b]"),
            "p" to listOf("[pa:] – [pɔ] - [pu:] -[pæ] – [pi:] ", "[a:p] – [ɔp] - [u:p] -[æp] – [i:p]"),
            "f" to listOf("[fa:] – [fɔ] - [fu:] -[fæ] – [fi:] ", "[a:f] – [ɔf] - [u:f] -[æf] – [i:f]"),         
            "v" to listOf("[va:] – [vɔ] - [vu:] -[væ] – [vi:] ", "[a:v] – [ɔv] - [u:v] -[æv] – [i:v]"),
            "m" to listOf("[ma:] – [mɔ:] - [mu:] -[mæ] – [mi:] ", "[a:m] – [ɔ:m] - [u:m] -[æm] – [i:m]")
        )
    }

    if (selectedLesson > 0) {
        val lessonPhonemeIds = lessonConfig[selectedLesson] ?: emptyList()
        val lessonPhonemes = lessonPhonemeIds.mapNotNull { id -> PHONEMES_LIST.find { it.id == id } }
        
        if (lessonPhonemes.isEmpty()) {
            onLessonChange(0)
            return@PracticeScreen
        }

        BackHandler {
            if (lessonStep > 0) onStepChange(lessonStep - 1) else onLessonChange(0)
        }

        AnimatedContent(
            targetState = lessonStep,
            label = "lessonStepContent",
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                }
            }
        ) { step: Int ->
            val screenIndex = step % 3
            val phonemeIndex = step / 3

            if (phonemeIndex < lessonPhonemes.size) {
                val phoneme = lessonPhonemes[phonemeIndex]
                
                when (screenIndex) {
                    0 -> {
                        LessonScreenWrapper(
                            step = step,
                            totalScreens = lessonPhonemes.size * 3,
                            selectedLesson = selectedLesson,
                            phoneme = phoneme,
                            onLessonChange = onLessonChange,
                            onStepChange = onStepChange,
                            language = language
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SoundToken(phoneme, onPlaySound)
                                Spacer(Modifier.height(16.dp))
                                DescriptionBlock(getLocalizedText(phoneme.id, phoneme.description, language, true))
                                Spacer(Modifier.height(24.dp))
                                LipAnimationPlaceholder(phoneme.id)
                                Spacer(Modifier.height(16.dp))
                                LipAnimationSteps(
                                    steps = lipAnimations[phoneme.id] ?: (if (language == "ua") listOf("губи закриті", "тиск", "різке відкриття") else listOf("губы закрыты", "давление", "резкое открытие")),
                                    language = language
                                )
                            }
                        }
                    }
                    1 -> {
                        LessonScreenWrapper(
                            step = step,
                            totalScreens = lessonPhonemes.size * 3,
                            selectedLesson = selectedLesson,
                            phoneme = phoneme,
                            onLessonChange = onLessonChange,
                            onStepChange = onStepChange,
                            language = language
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SoundToken(phoneme, onPlaySound)
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    t("pronounce_syllabes", language),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                    color = Color(0xFF22C55E)
                                )
                                Spacer(Modifier.height(32.dp))
                                SyllableExamplesBlock(syllableExamples[phoneme.id] ?: emptyList(), phoneme.id, phoneme.symbol, phoneme.category, onPlayWord)
                            }
                        }
                    }
                    2 -> {
                        LessonScreenWrapper(
                            step = step,
                            totalScreens = lessonPhonemes.size * 3,
                            selectedLesson = selectedLesson,
                            phoneme = phoneme,
                            onLessonChange = onLessonChange,
                            onStepChange = onStepChange,
                            language = language
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SoundToken(phoneme, onPlaySound)
                                Spacer(Modifier.height(32.dp))
                                Text(phoneme.example, fontSize = 52.sp, fontWeight = FontWeight.Black, color = Color.White)
                                TranscriptionText(
                                    transcription = phoneme.transcription,
                                    currentSymbol = phoneme.symbol,
                                    onSymbolClick = { symbol: String -> onPhonemeClick(symbol, phoneme.id) }
                                )
                                Text(getLocalizedText(phoneme.id, phoneme.translation, language, false), fontSize = 18.sp, fontStyle = FontStyle.Italic, color = Color.White.copy(0.6f))
                                Spacer(Modifier.height(24.dp))
                                WordImage(phoneme, onPlayWord)
                            }
                        }
                    }
                }
            } else {
                LessonCompleteScreen(selectedLesson, onLessonComplete, onLessonChange, onStepChange, lessonPhonemes.size * 3, language)
            }
        }
    } else {
        BackHandler {
            onBack()
        }
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(t("lessons", language), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Все согласные звуки
                item {
                    Text(
                        t("consonants", language),
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22C55E)
                    )
                }

                items((1..10).toList()) { lessonNum ->
                    lessonConfig[lessonNum]?.let { ids ->
                        LessonListItem(
                            lessonNum = lessonNum,
                            lessonPhonemeIds = ids,
                            completedLessons = completedLessons.value,
                            onLessonSelect = {
                                onLessonChange(lessonNum)
                                onStepChange(0)
                            }
                        )
                    }
                }
                
                // Все гласные звуки
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        t("vowels", language),
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22C55E)
                    )
                }
                
                items((11..15).toList()) { lessonNum ->
                    lessonConfig[lessonNum]?.let { ids ->
                        LessonListItem(
                            lessonNum = lessonNum,
                            lessonPhonemeIds = ids,
                            completedLessons = completedLessons.value,
                            onLessonSelect = {
                                onLessonChange(lessonNum)
                                onStepChange(0)
                            }
                        )
                    }
                }
            }

            // Horizontal Battery Progress Bar
            val totalLessonsCount = 15f
            BatteryProgressBar(progress = (completedLessons.value.size.toFloat() / totalLessonsCount), language = language)
        }
    }
}

@Composable
fun LessonListItem(
    lessonNum: Int,
    lessonPhonemeIds: List<String>,
    completedLessons: Set<Int>,
    onLessonSelect: () -> Unit
) {
    val isCompleted = completedLessons.contains(lessonNum)
    val isAvailable = lessonNum == 1 || (completedLessons.contains(lessonNum - 1))
    
    val phonemesSymbols = remember(lessonPhonemeIds) {
        lessonPhonemeIds.mapNotNull { id -> PHONEMES_LIST.find { it.id == id }?.symbol }.joinToString(", ") { "[$it]" }
    }
    
    Surface(
        onClick = { if (isAvailable) onLessonSelect() },
        shape = RoundedCornerShape(16.dp),
        color = if (isCompleted) Color(0xFF22C55E).copy(alpha = 0.1f) else if (isAvailable) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
        border = BorderStroke(1.dp, if (isCompleted) Color(0xFF22C55E).copy(alpha = 0.3f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth().height(64.dp).alpha(if (isAvailable) 1f else 0.4f)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Урок $lessonNum", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isAvailable) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(0.2f))
                Text(phonemesSymbols, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
            }
            if (isCompleted) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp), tint = Color(0xFF22C55E))
            }
            if (!isAvailable) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp).alpha(0.2f), tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun BatteryProgressBar(progress: Float, language: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color.Transparent
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                // Battery body
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(0.9f)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    // Battery fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .alpha(alpha)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF22C55E), Color(0xFF4ADE80))
                                ),
                                shape = RoundedCornerShape(5.dp)
                            )
                    )
                }
                // Battery tip
                Spacer(Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp, 10.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                )
            }
            Spacer(Modifier.height(4.dp))
                Text(
                    "${t("learning_progress", language)}: ${(progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22C55E).copy(alpha = alpha)
                )
        }
    }
}

@Composable
fun LessonScreenWrapper(
    step: Int,
    totalScreens: Int,
    selectedLesson: Int,
    phoneme: Phoneme,
    onLessonChange: (Int) -> Unit,
    onStepChange: (Int) -> Unit,
    language: String,
    content: @Composable () -> Unit
) {
    val screenIndex = step % 3
    val screenTitle = when(screenIndex) {
        0 -> t("sound_intro", language)
        1 -> t("sound_syllables", language)
        else -> t("sound_words", language)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(step) {
                    var accumulatedDrag = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (accumulatedDrag > 30f) {
                                if (step > 0) onStepChange(step - 1) else onLessonChange(0)
                            } else if (accumulatedDrag < -30f) {
                                onStepChange(step + 1)
                            }
                        },
                        onDragCancel = {
                            accumulatedDrag = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            accumulatedDrag += dragAmount
                        }
                    )
                }
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onLessonChange(0) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                }
                Column {
                    Text("Урок ${selectedLesson}. $screenTitle [${phoneme.symbol}]", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${t("step_word", language)} ${step + 1} ${t("of_total", language)} $totalScreens", fontSize = 11.sp, color = Color.White.copy(0.4f))
                }
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(totalScreens) { i: Int ->
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 8.dp else 4.dp)
                                .background(if (i == step) Color(0xFF22C55E) else Color.White.copy(0.1f), CircleShape)
                        )
                    }
                }
            }

            // Central Content
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            
            // Bottom Navigation Height Spacer (to avoid overlap if content is long)
            Spacer(Modifier.height(80.dp))
        }

            // Fixed Bottom Navigation
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (step > 0) {
                OutlinedButton(
                    onClick = { onStepChange(step - 1) },
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.1f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(t("back_btn", language), color = Color.White.copy(0.4f), fontSize = 13.sp)
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
            
            OutlinedButton(
                onClick = { onStepChange(step + 1) },
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF22C55E).copy(0.3f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(t("next_btn", language), color = Color.White.copy(0.8f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SoundToken(phoneme: Phoneme, onPlaySound: (String) -> Unit) {
    Surface(
        modifier = Modifier.size(80.dp).clickable { onPlaySound(phoneme.id) },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(0.05f),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("[${phoneme.symbol}]", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
        }
    }
}

@Composable
fun DescriptionBlock(description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(0.03f)
    ) {
        Text(description, textAlign = TextAlign.Center, color = Color(0xFF22C55E), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun LipAnimationPlaceholder(phonemeId: String) {
    val context = LocalContext.current
    // Naming convention: lip_b.png, lip_p.png, lip_th_unvoiced.png, etc.
    val resourceName = remember(phonemeId) { "lip_${phonemeId.replace('-', '_')}" }
    val resourceId = remember(resourceName) {
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
    Surface(
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(0.05f),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = if (resourceId != 0) resourceId else "https://picsum.photos/seed/${resourceName}/400/400",
                contentDescription = "Lip Animation: $resourceName",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun LipAnimationSteps(steps: List<String>, language: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(t("step_by_step_colon", language), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        steps.forEach { stepText ->
            Text(stepText, color = Color.White.copy(0.7f), fontSize = 14.sp)
        }
    }
}

@Composable
fun SyllableExamplesBlock(
    syllables: List<String>, 
    phonemeId: String, 
    symbol: String, 
    category: String, 
    onPlayWord: (String) -> Unit
) {
    val finalSyllables = remember(phonemeId, symbol, category) {
        val isVowel = category == "monophthong" || category == "diphthong" || phonemeId.startsWith("letter-a") || phonemeId.startsWith("letter-e") || phonemeId.startsWith("letter-i") || phonemeId.startsWith("letter-o") || phonemeId.startsWith("letter-u")
        val vowels = listOf("a", "o", "u", "e", "i")
        val consonants = listOf("b", "p", "f", "v", "m")
        val phoneme = PHONEMES_LIST.find { it.id == phonemeId }
        val cleanSymbol = if (phoneme != null) {
            val idMap = mapOf(
                "i-long" to "ee",
                "i-short" to "i",
                "u-short" to "u",
                "u-long" to "oo",
                "schwa" to "a",
                "er-long" to "ur",
                "o-long" to "or",
                "ae" to "a",
                "v-short" to "u",
                "a-long" to "ah",
                "o-short" to "o",
                "th-unvoiced" to "th",
                "th-voiced" to "th",
                "sh" to "sh",
                "zh" to "sh",
                "ch" to "ch",
                "ng" to "ng",
                "g-j" to "j"
            )
            val mapped = idMap[phoneme.id]
            if (mapped != null) {
                mapped
            } else {
                val label = if (phoneme.letterLabel.isNotEmpty()) phoneme.letterLabel else phoneme.symbol
                val lower = label.lowercase()
                if (lower.length == 2 && lower[0] == lower[1]) {
                    lower.substring(0, 1)
                } else {
                    lower
                }
            }
        } else {
            symbol.replace("ː", "").replace("ə", "a").trim().lowercase()
        }
        if (isVowel) {
            listOf(
                consonants.joinToString(" – ") { "${it}${cleanSymbol}" },
                consonants.joinToString(" – ") { "${cleanSymbol}${it}" }
            )
        } else {
            listOf(
                vowels.joinToString(" – ") { "${cleanSymbol}${it}" },
                vowels.joinToString(" – ") { "${it}${cleanSymbol}" }
            )
        }
    }

    val parseSyllables = { text: String ->
        val list = mutableListOf<String>()
        var current = ""
        var inside = false
        for (char in text) {
            if (char == '[') {
                inside = true
                current = "["
            } else if (char == ']') {
                if (inside) {
                    current += "]"
                    list.add(current)
                    inside = false
                }
            } else if (inside) {
                current += char
            }
        }
        if (list.isEmpty()) {
            text.split(" ", "–", "-").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            list
        }
    }

    val column1Syllables = remember(finalSyllables) {
        if (finalSyllables.isNotEmpty()) parseSyllables(finalSyllables[0]) else emptyList()
    }
    
    val column2Syllables = remember(finalSyllables) {
        if (finalSyllables.size > 1) parseSyllables(finalSyllables[1]) else emptyList()
    }
    
    val maxRows = maxOf(column1Syllables.size, column2Syllables.size)
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until maxRows) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Left column
                if (i < column1Syllables.size) {
                    SyllableItem(column1Syllables[i], onPlayWord)
                } else {
                    Spacer(Modifier.width(120.dp))
                }
                
                Spacer(Modifier.width(24.dp))
                
                // Right column
                if (i < column2Syllables.size) {
                    SyllableItem(column2Syllables[i], onPlayWord)
                } else {
                    Spacer(Modifier.width(120.dp))
                }
            }
        }
    }
}

@Composable
fun SyllableItem(syllable: String, onPlayWord: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .clickable { onPlayWord(syllable) },
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(0.05f),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Text(
            text = syllable,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WordImage(phoneme: Phoneme, onPlayWord: (String) -> Unit) {
    val context = LocalContext.current
    val imageModel = remember(phoneme.imageUrl, phoneme.example) {
        val resourceId = context.resources.getIdentifier(phoneme.imageUrl.toString(), "drawable", context.packageName)
        if (resourceId != 0) resourceId else {
            if (phoneme.imageUrl.toString().startsWith("http")) phoneme.imageUrl
            else "https://picsum.photos/seed/${phoneme.example}/400/400"
        }
    }
    Card(
        modifier = Modifier.size(130.dp).clickable { onPlayWord(phoneme.example) },
        shape = RoundedCornerShape(32.dp)
    ) {
        AsyncImage(model = imageModel, null, contentScale = ContentScale.Crop)
    }
}

@Composable
fun LessonCompleteScreen(
    selectedLesson: Int,
    onLessonComplete: (Int) -> Unit,
    onLessonChange: (Int) -> Unit,
    onStepChange: (Int) -> Unit,
    totalPhonemes: Int,
    language: String
) {
    var isConfirmed by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (isConfirmed) Color(0xFF22C55E) else Color.White.copy(alpha = 0.1f)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            t("lesson_learned", language),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            t("confirm_lesson", language),
            textAlign = TextAlign.Center,
            color = Color.White.copy(0.6f)
        )
        
        Spacer(Modifier.height(24.dp))
        
        Surface(
            modifier = Modifier
                .size(64.dp)
                .clickable { isConfirmed = !isConfirmed },
            shape = RoundedCornerShape(16.dp),
            color = if (isConfirmed) Color(0xFF22C55E).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
            border = BorderStroke(2.dp, if (isConfirmed) Color(0xFF22C55E) else Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isConfirmed) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFF22C55E), modifier = Modifier.size(32.dp))
                }
            }
        }
        
        Spacer(Modifier.height(48.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    onLessonComplete(selectedLesson)
                    onLessonChange(0)
                },
                enabled = isConfirmed,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(t("learned", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isConfirmed) Color.White else Color.White.copy(alpha = 0.3f))
            }
            
            OutlinedButton(
                onClick = { onStepChange(0) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {
                Text(t("repeat_lesson", language), color = Color.White.copy(0.6f))
            }
        }
    }
}

@Composable
fun AlphabetGridScreen(isDarkMode: Boolean, onBack: () -> Unit, onPlayLetter: (String) -> Unit) {
    BackHandler(onBack = onBack)
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("26 букв алфавита", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alphabet) { letter: String ->
                Surface(
                    modifier = Modifier.aspectRatio(1f).clickable { onPlayLetter(letter) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(letter, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(isDarkMode: Boolean, language: String, onOpenSettings: () -> Unit, onNavigate: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        IconButton(onClick = onOpenSettings, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(48.dp)) {
            Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(t("start_desc", language), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
            Text(t("start_sub", language), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(48.dp))

            // 26 букв алфавита
            MenuButton(
                title = t("menu_letters", language),
                subtitle = t("menu_letters_sub", language),
                label = "26",
                isDarkMode = isDarkMode,
                color = Color(0xFF22C55E),
                onClick = { onNavigate("alphabet_grid") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 44 звука
            MenuButton(
                title = t("menu_sounds", language),
                subtitle = t("menu_sounds_sub", language),
                label = "44",
                isDarkMode = isDarkMode,
                color = Color(0xFF22C55E),
                onClick = { onNavigate("phonemes") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Чтение букв
            MenuButton(
                title = t("menu_rules", language),
                subtitle = t("menu_rules_sub", language),
                label = "Aa",
                isDarkMode = isDarkMode,
                color = Color(0xFF22C55E),
                onClick = { onNavigate("alphabet") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Практика
            MenuButton(
                title = t("menu_practice", language),
                subtitle = t("menu_practice_sub", language),
                icon = Icons.Default.PlayArrow,
                isDarkMode = isDarkMode,
                color = Color(0xFF22C55E),
                onClick = { onNavigate("practice") }
            )
        }
    }
}

@Composable
fun MenuButton(
    title: String,
    subtitle: String,
    label: String? = null,
    icon: ImageVector? = null,
    isDarkMode: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(85.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
                Text(subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
            }
            if (label != null) {
                Text(label, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.1f))
            } else if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp).alpha(0.1f), tint = color)
            }
        }
    }
}

@Composable
fun PortionSelectionScreen(title: String, onBack: () -> Unit, onNavigate: (Int) -> Unit, language: String) {
    BackHandler(onBack = onBack)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var accumulatedDrag = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (accumulatedDrag > 30f) onBack()
                        },
                        onDragCancel = {
                            accumulatedDrag = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            accumulatedDrag += dragAmount
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Кнопка назад
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start).padding(16.dp)) {
                Text("←", fontSize = 24.sp, color = Color.White.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
            Text(t("select_portion", language), fontSize = 14.sp, color = Color.White.copy(alpha = 0.4f), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(64.dp))

            // Кнопка Порция 1
            Button(
                onClick = { onNavigate(1) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(80.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(t("portion_1", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(t("portion_1_desc", language), fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка Порция 2
            Button(
                onClick = { onNavigate(2) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(80.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(t("portion_2", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(t("portion_2_desc", language), fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlphabetScreen(isDarkMode: Boolean, initialPhonemeId: String? = null, onBack: () -> Unit, onPlayWord: (String) -> Unit, onPlaySound: (String, Boolean) -> Unit, onNavigateToSounds: (String, String) -> Unit, onPhonemeChange: (String) -> Unit, language: String) {
    BackHandler(onBack = onBack)

    // Категории для алфавита
    val consonants = listOf("b", "p", "v", "f", "s", "s-z", "letter-c", "letter-c-k", "z", "k", "g", "g-j", "m", "t", "d", "n", "l", "j", "y", "h", "w", "r", "letter-q", "letter-x", "sh", "ch", "th-unvoiced", "th-voiced", "ng", "comb-sio")
    val vowels = listOf("letter-a-long", "letter-a-short", "letter-e-long", "letter-e-short", "letter-i-long", "letter-i-short", "letter-o-long", "letter-o-short", "letter-u-long", "letter-u-short", "comb-ar", "comb-er", "comb-or", "comb-ir", "comb-oo", "comb-oi", "comb-ou", "comb-ear", "comb-ure", "comb-air")

    val globalOrderedList = remember {
        val c = PHONEMES_LIST.filter { it: Phoneme -> it.id in consonants }.sortedBy { it: Phoneme -> consonants.indexOf(it.id) }
        val v = PHONEMES_LIST.filter { it: Phoneme -> it.id in vowels }.sortedBy { it: Phoneme -> vowels.indexOf(it.id) }
        c + v
    }

    var selectedPhoneme by remember {
        val initial = if (initialPhonemeId != null) globalOrderedList.find { it.id == initialPhonemeId } else null
        mutableStateOf<Phoneme>(initial ?: globalOrderedList[0])
    }
    var activeCategory by remember { mutableStateOf<String>("СОГЛАСНЫЕ") }

    LaunchedEffect(selectedPhoneme) {
        activeCategory = if (selectedPhoneme.id in consonants) "СОГЛАСНЫЕ" else "ГЛАСНЫЕ"
    }

    val filteredPhonemes = when (activeCategory) {
        "СОГЛАСНЫЕ" -> globalOrderedList.filter { it.id in consonants }
        "ГЛАСНЫЕ" -> globalOrderedList.filter { it.id in vowels }
        else -> globalOrderedList
    }

    val pagerState = rememberPagerState(initialPage = globalOrderedList.indexOf(selectedPhoneme)) { globalOrderedList.size }

    LaunchedEffect(pagerState.currentPage) {
        val newPhoneme = globalOrderedList[pagerState.currentPage]
        selectedPhoneme = newPhoneme
        onPhonemeChange(newPhoneme.id)
    }

    LaunchedEffect(selectedPhoneme) {
        val index = globalOrderedList.indexOf(selectedPhoneme)
        if (index != pagerState.currentPage) {
            pagerState.scrollToPage(index)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
    ) {
        // --- ВЕРХНЯЯ ПАНЕЛЬ ---
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("←", fontSize = 24.sp, color = Color.White.copy(alpha = 0.6f))
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(80.dp)
                        .clickable { onPlaySound(selectedPhoneme.id, true) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "${t("button_letter", language)} ${selectedPhoneme.letterLabel}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                    }
                }

                Text("-", color = Color.White.copy(alpha = 0.2f), fontSize = 24.sp)

                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(80.dp)
                        .clickable { onPlaySound(selectedPhoneme.id, false) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "${t("button_sound", language)} [${selectedPhoneme.symbol}]",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                    }
                }
            }
        }

        // --- ЦЕНТРАЛЬНАЯ ЧАСТЬ ---
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                val phoneme = globalOrderedList[page]
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Описание удалено по просьбе пользователя
                    Spacer(modifier = Modifier.height(22.dp))

                    Text(text = phoneme.example, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    TranscriptionText(
                        transcription = phoneme.transcription,
                        currentSymbol = phoneme.symbol,
                        onSymbolClick = { symbol: String -> onNavigateToSounds(symbol, phoneme.id) }
                    )
                    Text(text = getLocalizedText(phoneme.id, phoneme.translation, language, false), fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color.White.copy(alpha = 0.6f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        val context = LocalContext.current
                        val resourceId = remember(phoneme.imageUrl) {
                            context.resources.getIdentifier(phoneme.imageUrl.toString(), "drawable", context.packageName)
                        }
                        AsyncImage(
                            model = if (resourceId != 0) resourceId else phoneme.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clickable { onPlayWord(phoneme.example) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // --- НИЖНЯЯ ЧАСТЬ (СЕТКА БУКВ) ---
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            color = Color.White.copy(alpha = 0.02f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("СОГЛАСНЫЕ", "ГЛАСНЫЕ")
                    tabs.forEach { label: String ->
                        Surface(
                            modifier = Modifier.weight(1f).clickable { activeCategory = label }.padding(horizontal = 4.dp),
                            shape = RoundedCornerShape(99.dp),
                            color = if (activeCategory == label) Color(0xFF22C55E).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
                        ) {
                            Text(
                                text = if (label == "СОГЛАСНЫЕ") t("consonants_caps", language) else t("vowels_caps", language),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().height(240.dp).padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPhonemes) { phoneme: Phoneme ->
                        Surface(
                            modifier = Modifier.aspectRatio(1f).clickable {
                                selectedPhoneme = phoneme
                                onPlaySound(phoneme.id, true)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedPhoneme.id == phoneme.id) Color(0xFF22C55E) else Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = phoneme.letterLabel, fontSize = 18.sp, color = if (selectedPhoneme.id == phoneme.id) Color.White else Color.White.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhonemicApp(
    isDarkMode: Boolean,
    initialPhonemeId: String? = null,
    onBack: () -> Unit,
    onPlayWord: (String) -> Unit,
    onPlaySound: (String) -> Unit,
    onNavigateToPhoneme: (String) -> Unit,
    onPhonemeChange: (String) -> Unit,
    language: String
) {
    // Обработка системной кнопки "Назад"
    BackHandler(onBack = onBack)

    // --- ОПРЕДЕЛЕНИЕ ГРУПП ЗВУКОВ ---
    val consonants = listOf("p", "b", "v", "f", "s", "z", "k", "g", "m", "t", "d", "n", "l", "j", "y", "h", "w", "r", "ch", "sh", "zh", "th-unvoiced", "th-voiced", "ng")
    val vowels = listOf("ei", "ae", "i-long", "e", "ai", "i-short", "ou", "o-short", "u-long", "v-short", "a-long", "schwa", "o-long", "er-long", "u-short", "oi", "au", "ie", "ue", "ea")

    // Глобальный список для свайпа
    val globalOrderedList = remember {
        val c = PHONEMES_LIST.filter { p: Phoneme -> p.id in consonants }.sortedBy { p: Phoneme -> consonants.indexOf(p.id) }
        val v = PHONEMES_LIST.filter { p: Phoneme -> p.id in vowels }.sortedBy { p: Phoneme -> vowels.indexOf(p.id) }
        c + v
    }

    // Состояние выбранного звука
    var selectedPhoneme by remember(initialPhonemeId) {
        val initial = if (initialPhonemeId != null) {
            globalOrderedList.find { it.id == initialPhonemeId } ?: globalOrderedList[0]
        } else {
            globalOrderedList[0]
        }
        mutableStateOf<Phoneme>(initial)
    }
    // Состояние активной вкладки
    var activeCategory by remember { mutableStateOf<String>("СОГЛАСНЫЕ") }

    // Автоматическое переключение вкладок при смене звука (свайпом)
    LaunchedEffect(selectedPhoneme) {
        activeCategory = if (selectedPhoneme.id in consonants) "СОГЛАСНЫЕ" else "ГЛАСНЫЕ"
    }

    // Фильтрация звуков по вкладкам
    val filteredPhonemes = when (activeCategory) {
        "СОГЛАСНЫЕ" -> globalOrderedList.filter { it.id in consonants }
        "ГЛАСНЫЕ" -> globalOrderedList.filter { it.id in vowels }
        else -> globalOrderedList
    }

    // Состояние для свайпа (пейджер)
    val pagerState = rememberPagerState(initialPage = globalOrderedList.indexOf(selectedPhoneme)) { globalOrderedList.size }

    // Синхронизация пейджера и выбранного звука
    LaunchedEffect(pagerState.currentPage) {
        val newPhoneme = globalOrderedList[pagerState.currentPage]
        selectedPhoneme = newPhoneme
        onPhonemeChange(newPhoneme.id)
    }

    LaunchedEffect(selectedPhoneme) {
        val index = globalOrderedList.indexOf(selectedPhoneme)
        if (index != pagerState.currentPage) {
            pagerState.scrollToPage(index)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var accumulatedDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (accumulatedDrag > 30f && pagerState.currentPage == 0) onBack()
                    },
                    onDragCancel = {
                        accumulatedDrag = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        accumulatedDrag += dragAmount
                    }
                )
            }
    ) {
        // --- ВЕРХНЯЯ ПАНЕЛЬ (Кнопка назад и Звук) ---
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("←", fontSize = 24.sp, color = Color.White.copy(alpha = 0.6f))
            }

            // --- КВАДРАТ СО ЗВУКОМ (ПЕРЕМЕЩЕН ВЫШЕ) ---
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.Center)
                    .clickable { onPlaySound(selectedPhoneme.id) },
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = selectedPhoneme.symbol, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                }
            }
        }

        // --- ЦЕНТРАЛЬНАЯ ЧАСТЬ (ОПИСАНИЕ И КАРТИНКА) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val phoneme = globalOrderedList[page]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Убрали квадрат отсюда, он теперь сверху

                    // --- ОТСТУП МЕЖДУ КВАДРАТОМ С СИМВОЛОМ И ОПИСАНИЕМ (0.dp) ---
                    Spacer(modifier = Modifier.height(0.dp))

                    // --- БЛОК ОПИСАНИЯ ---
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.05f)
                    ) {
                        Text(
                            text = getLocalizedText(phoneme.id, phoneme.description, language, true),
                            fontSize = 11.sp,
                            color = Color(0xFF22C55E),
                            textAlign = TextAlign.Center,
                            minLines = 2,
                            maxLines = 2,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    // --- ОТСТУП МЕЖДУ ОПИСАНИЕМ И СЛОВОМ (12.dp) ---
                    Spacer(modifier = Modifier.height(12.dp))

                    // --- ТЕКСТОВАЯ ИНФОРМАЦИЯ ---
                    Text(text = phoneme.example, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    // ШРИФТ ТРАНСКРИПЦИИ (18.sp, Полупрозрачный белый)
                    TranscriptionText(
                        transcription = phoneme.transcription,
                        currentSymbol = phoneme.symbol,
                        onSymbolClick = { symbol: String -> onNavigateToPhoneme(symbol) }
                    )
                    // ШРИФТ ПЕРЕВОДА (13.sp, Курсив, Полупрозрачный белый)
                    Text(text = getLocalizedText(phoneme.id, phoneme.translation, language, false), fontSize = 13.sp, fontStyle = FontStyle.Italic, color = Color.White.copy(alpha = 0.6f))

                    Spacer(modifier = Modifier.height(8.dp)) // ОТСТУП ПЕРЕД КАРТИНКОЙ (8.dp)

                    // --- КАРТИНКА ---
                    Surface(
                        modifier = Modifier.size(110.dp), // РАЗМЕР КАРТИНКИ (110.dp)
                        shape = RoundedCornerShape(24.dp), // СКРУГЛЕНИЕ УГЛОВ КАРТИНКИ (24.dp)
                        color = Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        val context = LocalContext.current
                        val resourceId = remember(phoneme.imageUrl) {
                            val name = phoneme.imageUrl.toString() // Убрали substringBeforeLast так как расширения больше нет
                            context.resources.getIdentifier(name, "drawable", context.packageName)
                        }
                        AsyncImage(
                            model = if (resourceId != 0) resourceId else phoneme.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clickable { onPlayWord(phoneme.example) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // --- НИЖНЯЯ ЧАСТЬ (СЕТКА ЗВУКОВ) ---
        // Убрали фиксированный weight(0.33f), теперь занимает сколько нужно
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            color = Color.White.copy(alpha = 0.02f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column {
                // --- ВКЛАДКИ (ТАБЫ) ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("СОГЛАСНЫЕ", "ГЛАСНЫЕ")
                    tabs.forEach { label: String ->
                        Surface(
                            modifier = Modifier.clickable { activeCategory = label },
                            shape = RoundedCornerShape(99.dp),
                            // ЦВЕТ АКТИВНОЙ ВКЛАДКИ
                            color = if (activeCategory == label) Color(0xFF22C55E) else Color.White.copy(alpha = 0.05f)
                        ) {
                            Text(
                                text = (if (label == "СОГЛАСНЫЕ") t("consonants_caps", language) else t("vowels_caps", language)).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // --- СЕТКА ЗВУКОВ ---
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7), // КОЛИЧЕСТВО КОЛОНОК В СЕТКЕ
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp) // Увеличили со 160.dp до 240.dp для соответствия алфавиту
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPhonemes) { phoneme: Phoneme ->
                        Surface(
                            modifier = Modifier.aspectRatio(1f).clickable {
                                selectedPhoneme = phoneme
                                onPlaySound(phoneme.id) // ВОСПРОИЗВЕДЕНИЕ ПРИ НАЖАТИИ В СЕТКЕ
                            },
                            shape = RoundedCornerShape(12.dp),
                            // ЦВЕТ ВЫДЕЛЕННОГО ЗВУКА В СЕТКЕ
                            color = if (selectedPhoneme.id == phoneme.id) Color(0xFF22C55E) else Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = phoneme.symbol, fontSize = 18.sp, color = if (selectedPhoneme.id == phoneme.id) Color.White else Color.White.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onResetProgress: () -> Unit,
    onResetSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t("settings", language)) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(t("dark_theme", language))
                    Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
                }
                Spacer(Modifier.height(16.dp))

                Text(t("settings_lang", language), fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onLanguageChange("ru") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (language == "ru") Color(0xFF16A34A) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            contentColor = if (language == "ru") Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text("Русский")
                    }
                    Button(
                        onClick = { onLanguageChange("ua") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (language == "ua") Color(0xFF16A34A) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            contentColor = if (language == "ua") Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text("Українська")
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onResetProgress,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red)
                ) {
                    Text(t("reset_progress", language))
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onResetSettings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(t("reset_all", language))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t("close", language))
            }
        }
    )
}
