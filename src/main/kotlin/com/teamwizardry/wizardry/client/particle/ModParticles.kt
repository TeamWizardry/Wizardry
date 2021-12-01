package com.teamwizardry.wizardry.client.particle

import com.teamwizardry.librarianlib.math.Easing.Companion.easeInBack
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInBounce
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInCirc
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInCubic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInElastic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInExpo
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutBack
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutBounce
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutCirc
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutCubic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutElastic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutExpo
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutQuad
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutQuart
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutQuint
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInOutSine
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInQuad
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInQuart
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInQuint
import com.teamwizardry.librarianlib.math.Easing.Companion.easeInSine
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutBack
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutBounce
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutCirc
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutCubic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutElastic
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutExpo
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutQuad
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutQuart
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutQuint
import com.teamwizardry.librarianlib.math.Easing.Companion.easeOutSine
import com.teamwizardry.librarianlib.math.Easing.Companion.linear


object ModParticles {

    val easings = listOf(
        linear, easeInSine, easeOutSine, easeInOutSine,
        easeInQuad,
        easeOutQuad,
        easeInOutQuad,
        easeInCubic,
        easeOutCubic,
        easeInOutCubic,
        easeInQuart,
        easeOutQuart,
        easeInOutQuart,
        easeInQuint,
        easeOutQuint,
        easeInOutQuint,
        easeInExpo,
        easeOutExpo,
        easeInOutExpo,
        easeInCirc,
        easeOutCirc,
        easeInOutCirc,
        easeInBack,
        easeOutBack,
        easeInOutBack,
        easeInElastic,
        easeOutElastic,
        easeInOutElastic,
        easeInBounce,
        easeOutBounce,
        easeInOutBounce,
    )

    val physicsGlitter = ParticlePhysicsGlitter()
}