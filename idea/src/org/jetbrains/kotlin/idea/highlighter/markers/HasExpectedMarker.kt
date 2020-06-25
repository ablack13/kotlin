/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.highlighter.markers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.idea.caches.project.implementedDescriptors
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.idea.core.toDescriptor
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.util.expectedDeclaration
import org.jetbrains.kotlin.idea.util.expectedDescriptors
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.multiplatform.ExpectedActualResolver

fun getExpectedDeclarationTooltip(declaration: KtDeclaration): String? {
    val descriptor = declaration.toDescriptor() as? MemberDescriptor ?: return null

    if (descriptor.expectedDescriptors().isEmpty()) return null

    return KotlinBundle.message("highlighter.tool.tip.has.declaration.in.common.module")
}

fun KtDeclaration.allNavigatableExpectedDeclarations(): List<KtDeclaration> =
    listOfNotNull(expectedDeclaration()) + findMarkerBoundDeclarations().mapNotNull { it.expectedDeclaration() }

fun KtDeclaration.navigateToExpectedTitle() = KotlinBundle.message("highlighter.title.choose.expected.for", name.toString())

fun KtDeclaration.navigateToExpectedUsagesTitle() = KotlinBundle.message("highlighter.title.expected.for", name.toString())

fun buildNavigateToExpectedDeclarationsPopup(element: PsiElement?): NavigationPopupDescriptor? {
    return element?.markerDeclaration?.let {
        val navigatableExpectedDeclarations = it.allNavigatableExpectedDeclarations()
        if (navigatableExpectedDeclarations.isEmpty()) return null
        return NavigationPopupDescriptor(
            navigatableExpectedDeclarations,
            it.navigateToExpectedTitle(),
            it.navigateToExpectedUsagesTitle(),
            ActualExpectedPsiElementCellRenderer()
        )
    }
}