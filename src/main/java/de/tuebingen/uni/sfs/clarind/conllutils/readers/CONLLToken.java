package de.tuebingen.uni.sfs.clarind.conllutils.readers;

// Copyright 2008, 2013 Daniel de Kok
//
// This class was borrowed from the Jitar tagger.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Instances of <i>CONLLToken</i> represent a token from a CONLL corpus.
 * At the bare minimum a token contains a position/identifier and the
 * actual token. The token can optionally be annotated with: a lemma,
 * a course-grained POS tag, a fine-grained POS tag, features, the head
 * of the token, the dependency relation to the head, the projective head
 * of the token, and the dependency relation to the projective head.
 */
public class CONLLToken {
    private final int position;
    private final String form;
    private final Optional<String> lemma;
    private final Optional<String> coursePOSTag;
    private final Optional<String> posTag;
    private final Optional<String> features;
    private final Optional<Integer> head;
    private final Optional<String> depRel;
    private final Optional<Integer> pHead;
    private final Optional<String> pDepRel;

    public CONLLToken(int position, String form, Optional<String> lemma, Optional<String> coursePOSTag,
                      Optional<String> posTag, Optional<String> features, Optional<Integer> head,
                      Optional<String> depRel, Optional<Integer> pHead, Optional<String> pDepRel) {
        this.position = position;
        this.form = form;
        this.lemma = lemma;
        this.coursePOSTag = coursePOSTag;
        this.posTag = posTag;
        this.features = features;
        this.head = head;
        this.depRel = depRel;
        this.pHead = pHead;
        this.pDepRel = pDepRel;
    }

    public int getPosition() {
        return position;
    }

    public String getForm() {
        return form;
    }

    public Optional<String> getLemma() {
        return lemma;
    }

    public Optional<String> getCoursePOSTag() {
        return coursePOSTag;
    }

    public Optional<String> getPosTag() {
        return posTag;
    }

    public Optional<String> getFeatures() {
        return features;
    }

    public Optional<Integer> getHead() {
        return head;
    }

    public Optional<String> getDepRel() {
        return depRel;
    }

    public Optional<Integer> getPHead() {
        return pHead;
    }

    public Optional<String> getPDepRel() {
        return pDepRel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CONLLToken that = (CONLLToken) o;

        if (position != that.position) return false;
        if (coursePOSTag != null ? !coursePOSTag.equals(that.coursePOSTag) : that.coursePOSTag != null) return false;
        if (depRel != null ? !depRel.equals(that.depRel) : that.depRel != null) return false;
        if (features != null ? !features.equals(that.features) : that.features != null) return false;
        if (form != null ? !form.equals(that.form) : that.form != null) return false;
        if (head != null ? !head.equals(that.head) : that.head != null) return false;
        if (lemma != null ? !lemma.equals(that.lemma) : that.lemma != null) return false;
        if (pDepRel != null ? !pDepRel.equals(that.pDepRel) : that.pDepRel != null) return false;
        if (pHead != null ? !pHead.equals(that.pHead) : that.pHead != null) return false;
        if (posTag != null ? !posTag.equals(that.posTag) : that.posTag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (form != null ? form.hashCode() : 0);
        result = 31 * result + (lemma != null ? lemma.hashCode() : 0);
        result = 31 * result + (coursePOSTag != null ? coursePOSTag.hashCode() : 0);
        result = 31 * result + (posTag != null ? posTag.hashCode() : 0);
        result = 31 * result + (features != null ? features.hashCode() : 0);
        result = 31 * result + (head != null ? head.hashCode() : 0);
        result = 31 * result + (depRel != null ? depRel.hashCode() : 0);
        result = 31 * result + (pHead != null ? pHead.hashCode() : 0);
        result = 31 * result + (pDepRel != null ? pDepRel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String fields[] = {Integer.toString(position), form, lemma.or("_"), coursePOSTag.or("_"), posTag.or("_"),
                features.or("_"), intToStringOr(head, "_"), depRel.or("_"), intToStringOr(pHead, "_"), pDepRel.or("_")};
        return StringUtils.join(fields, "\t");
    }

    private String intToStringOr(Optional<Integer> v, String or) {
        return v.isPresent() ? Integer.toString(v.get()) : or;

    }
}
