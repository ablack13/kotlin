/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:JvmName("ExpectActualResolutionApi")

package org.jetbrains.kotlin.resolve.multiplatform

import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.module

/**
 * Convenience shortcuts
 *
 * Essentially they just delegate to [ExpectedActualResolver], and perform some trivial
 * utility-work (like resolving only compatible expects/actuals, etc.)
 */

// FIXME(dsavvinov): review clients, as they won't work properly in HMPP projects
fun MemberDescriptor.findCompatibleActualForExpected(platformModule: ModuleDescriptor = this.module): List<MemberDescriptor> =
    ExpectedActualResolver.findActualForExpected(this, platformModule)
        ?.get(ExpectedActualResolver.Compatibility.Compatible).orEmpty()

fun MemberDescriptor.findCompatibleExpectedForActual(commonModule: ModuleDescriptor = this.module): List<MemberDescriptor> =
    ExpectedActualResolver.findExpectedForActual(this, commonModule)
        ?.get(ExpectedActualResolver.Compatibility.Compatible).orEmpty()

/**
 * Returns compatible actuals, if there are at least one; returns all incompatible actuals otherwise
 */
fun MemberDescriptor.findAnyActualForExpected(platformModule: ModuleDescriptor): List<MemberDescriptor> =
    ExpectedActualResolver.findActualForExpected(this, platformModule).flattenPreferringCompatible()

/**
 * Returns compatible expects, if there are at least one; returns all incompatible expects otherwise
 */
fun MemberDescriptor.findAnyExpectForActual(commonModule: ModuleDescriptor): List<MemberDescriptor> =
    ExpectedActualResolver.findExpectedForActual(this, commonModule).flattenPreferringCompatible()

private fun Map<ExpectedActualResolver.Compatibility, List<MemberDescriptor>>?.flattenPreferringCompatible(): List<MemberDescriptor> =
    (this?.get(ExpectedActualResolver.Compatibility.Compatible)
        ?: this?.values?.flatten()
        ?: emptyList())
