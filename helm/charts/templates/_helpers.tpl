{{/*
Purchases API
*/}}
{{- define "purchases.name" -}}
{{- printf "%s-%s" .Release.Name "purchases" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "purchases.deploy" -}}
{{- printf "%s-%s-%s"  .Release.Name "purchases" "deploy" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "purchases.service" -}}
{{- printf "%s-%s-%s"  .Release.Name "purchases" "service" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "purchases.port" -}}
{{- printf "%s-%s-%s"  .Release.Name "purchases" "port" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
MySQL
*/}}
{{- define "mysql.service" -}}
{{- if .Values.mysql.create }}
{{- printf "%s-%s-%s" .Release.Name "mysql" "service" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s" .Values.mysql.host }}
{{- end }}
{{- end }}

{{- define "mysql.name" -}}
{{- printf "%s-%s" .Release.Name "mysql" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "mysql.deploy" -}}
{{- printf "%s-%s-%s" .Release.Name "mysql" "deploy" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "mysql.pvc" -}}
{{- printf "%s-%s-%s-%s" "purchases" .Values.namespace "mysql" "pvc" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "mysql.port" -}}
{{- printf "%s-%s-%s" .Release.Name "mysql" "port" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Secrets
*/}}
{{- define "purchases.secrets" -}}
{{- printf "%s-%s-%s" .Release.Name "purchases" "secrets" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Zookeeper
*/}}
{{- define "zookeeper.pdb" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "pdb" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.headless.service" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "hs" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.client.service" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "cs" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.name" -}}
{{- printf "%s-%s" .Release.Name "zk" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.server.port" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "sp" | replace "+" "_" | trunc 15 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.le.port" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "lep" | replace "+" "_" | trunc 15 | trimSuffix "-" }}
{{- end }}

{{- define "zookeeper.client.port" -}}
{{- printf "%s-%s-%s" .Release.Name "zk" "cp" | replace "+" "_" | trunc 15 | trimSuffix "-" }}
{{- end }}


{{/*
Kafka
*/}}
{{- define "kafka.service" -}}
{{- printf "%s-%s-%s" .Release.Name "kafka" "hs" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "kafka.pdb" -}}
{{- printf "%s-%s-%s" .Release.Name "kafka" "pdb" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "kafka.name" -}}
{{- printf "%s-%s" .Release.Name "kafka" | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "kafka.port" -}}
{{- printf "%s-%s-%s" .Release.Name "kafka" "port" | replace "+" "_" | trunc 15 | trimSuffix "-" }}
{{- end }}