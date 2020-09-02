### HELM CHARTS
# ¯¯¯¯¯¯¯¯

helm.charts.mysql.install: ##
	helm upgrade -i mysql attachments/kubernetes/mysql --namespace=accounts --create-namespace --wait

helm.charts.account-service.install: ## Deploy application (example: make helm.charts.account-service.install version=1.1.2)
	helm upgrade -i account-service attachments/kubernetes/account-service --set image.tag=${version} --namespace accounts --create-namespace

helm.charts.all.services.install: helm.charts.mysql.install helm.charts.account-service.install ## Deploy all (example: make helm.charts.all.services.install version=1.1.2 env=dev)
	echo "Finished deploy services"