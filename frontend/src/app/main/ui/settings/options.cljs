;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; This Source Code Form is "Incompatible With Secondary Licenses", as
;; defined by the Mozilla Public License, v. 2.0.
;;
;; Copyright (c) 2020 UXBOX Labs SL

(ns app.main.ui.settings.options
  (:require
   [app.common.spec :as us]
   [app.main.data.messages :as dm]
   [app.main.data.users :as du]
   [app.main.refs :as refs]
   [app.main.store :as st]
   [app.main.ui.components.forms :as fm]
   [app.main.ui.icons :as i]
   [app.util.dom :as dom]
   [app.util.i18n :as i18n :refer [t tr]]
   [cljs.spec.alpha :as s]
   [rumext.alpha :as mf]))

(s/def ::lang (s/nilable ::us/not-empty-string))
(s/def ::theme (s/nilable ::us/not-empty-string))

(s/def ::options-form
  (s/keys :opt-un [::lang ::theme]))

(defn- on-error
  [form error]
  (st/emit! (dm/error (tr "errors.generic"))))

(defn- on-success
  [form]
  (st/emit! (dm/success (tr "notifications.profile-saved"))))

(defn- on-submit
  [form event]
  (let [data  (:clean-data @form)
        mdata {:on-success (partial on-success form)
               :on-error (partial on-error form)}]
    (st/emit! (du/update-profile (with-meta data mdata)))))

(mf/defc options-form
  [{:keys [locale] :as props}]
  (let [profile (mf/deref refs/profile)
        form    (fm/use-form :spec ::options-form
                             :initial profile)]
    [:& fm/form {:class "options-form"
                 :on-submit on-submit
                 :form form}

     [:h2 (t locale "labels.language")]

     [:div.fields-row
      [:& fm/select {:options [{:label "English" :value "en"}
                               {:label "Français" :value "fr"}
                               {:label "Español" :value "es"}
                               {:label "Русский" :value "ru"}]
                     :label (t locale "dashboard.select-ui-language")
                     :default "en"
                     :name :lang}]]

     [:h2 (t locale "dashboard.theme-change")]
     [:div.fields-row
      [:& fm/select {:label (t locale "dashboard.select-ui-theme")
                     :name :theme
                     :default "default"
                     :options [{:label "Default" :value "default"}]}]]
     ;; --- Feedback section
     [:h2 "Email"]
     [:p "Please describe the reason of your email, specifying if is an issue, an idea or a doubt. A member of our team will respond as soon as possible."]
     [:div.fields-row
      [:div.custom-input
       [:label "Subject"]
       [:input {:type "text" :name "Subject" :label "Subject"}]]]
     [:div.fields-row
      [:div.custom-input
       [:label "Description"]
       [:textarea {:type "text" :name "Subject" :label "Description" :rows "5"}]]]
     [:input.btn-primary.btn-large {:name "submit" :type "submit" :value "Send"}]

     [:hr]

     [:h2 "Team discussions"]
     [:p "Join Penpot team collaborative communication forum."]
     [:p "You can ask and answer questions, have open-ended conversations, and follow along on decisions affecting the project."]
     [:a.btn-secondary.btn-large {:href "https://github.com/penpot/penpot/discussions" :target "_blank"} "Go to discussions"]

     [:hr]

     [:h2 "Gitter"]
     [:p "Feeling like talking? Chat with us at Gitter"]
     [:a.btn-secondary.btn-large {:href "#" :target "_blank"} "Start a chat"]
     ;; --- Feedback section
     
     [:& fm/submit-button
      {:label (t locale "dashboard.update-settings")}]]))

;; --- Password Page

(mf/defc options-page
  [{:keys [locale]}]
  [:div.dashboard-settings
   [:div.form-container
    [:& options-form {:locale locale}]]])
