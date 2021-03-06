package net.lacnic.siselecciones.admin.dashboard.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.lacnic.siselecciones.admin.app.Contexto;
import net.lacnic.siselecciones.admin.web.bases.DashboardAdminBasePage;
import net.lacnic.siselecciones.admin.wicket.util.UtilsParameters;
import net.lacnic.siselecciones.dominio.Auditor;
import net.lacnic.siselecciones.dominio.TemplateEleccion;
import net.lacnic.siselecciones.dominio.UsuarioPadron;

@AuthorizeInstantiation("siselecciones-only-one")
public class DashboardPreviewDestinatarios extends DashboardAdminBasePage {

	private static final long serialVersionUID = 9205049748099839214L;

	private static final Logger appLogger = LogManager.getLogger("webAdminAppLogger");

	private List<Auditor> usuariosAuditor = new ArrayList<>();
	private List<UsuarioPadron> usuariosPadron = new ArrayList<>();
	int cantidad;

	public DashboardPreviewDestinatarios(final TemplateEleccion template, PageParameters params) {
		super(params);

		setOutputMarkupPlaceholderTag(true);
		add(new FeedbackPanel("feedback"));

		List lista;
		try {
			lista = Contexto.getInstance().getManagerBeanRemote().obtenerDestinatariosTipoDestinatario(template);

			cantidad = 0;
			if (lista != null && !lista.isEmpty()) {
				cantidad = lista.size();
				if (lista.get(0) instanceof UsuarioPadron) {
					usuariosPadron = lista;
				} else if (lista.get(0) instanceof Auditor) {
					usuariosAuditor = lista;
				}
			}

			WebMarkupContainer container = new WebMarkupContainer("containerAuditores");
			container.add(new Label("cantidad", new PropertyModel<>(DashboardPreviewDestinatarios.this, "cantidad")));

			ListView<Auditor> dataViewAuditor = new ListView<Auditor>("lista", usuariosAuditor) {
				private static final long serialVersionUID = 1786359392545666490L;

				@Override
				protected void populateItem(ListItem<Auditor> item) {
					final Auditor a = item.getModelObject();
					try {
						item.add(new Label("nombre", a.getNombre()));
						item.add(new Label("email", a.getMail()));

						item.add(new AjaxLink<Void>("eliminar") {

							private static final long serialVersionUID = 6264547412680468966L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								item.remove();
								usuariosAuditor.remove(a);
								cantidad = (usuariosAuditor.size());
								target.add(container);
							}
						});

					} catch (Exception e) {
						appLogger.error(e);
						error(e.getMessage());
					}
				}
			};

			container.setOutputMarkupPlaceholderTag(true);
			container.add(dataViewAuditor);
			add(container);
			container.setVisible(!usuariosAuditor.isEmpty());

			WebMarkupContainer container2 = new WebMarkupContainer("containerUsuariosPadron");
			container2.add(new Label("cantidad", new PropertyModel<>(DashboardPreviewDestinatarios.this, "cantidad")));

			ListView<UsuarioPadron> dataViewUsuarioPadron = new ListView<UsuarioPadron>("lista", usuariosPadron) {
				private static final long serialVersionUID = 1786359392545666490L;

				@Override
				protected void populateItem(ListItem<UsuarioPadron> item) {
					final UsuarioPadron a = item.getModelObject();
					try {
						item.add(new Label("nombre", a.getNombre()));
						item.add(new Label("email", a.getMail()));
						item.add(new Label("pais", a.getPais()));
						item.add(new Label("idioma", a.getIdioma()));

						item.add(new AjaxLink<Void>("eliminar") {

							private static final long serialVersionUID = -3818107825435540166L;

							@Override
							public void onClick(AjaxRequestTarget target) {

								item.remove();
								usuariosPadron.remove(a);
								cantidad = (usuariosPadron.size());
								target.add(container2);

							}
						});

					} catch (Exception e) {
						appLogger.error(e);
						error(e.getMessage());
					}
				}
			};

			container2.setOutputMarkupPlaceholderTag(true);
			container2.add(dataViewUsuarioPadron);
			add(container2);
			container2.setVisible(!usuariosPadron.isEmpty());

			add(new Link<Void>("enviar") {

				private static final long serialVersionUID = -5702257874045515363L;

				@Override
				public void onClick() {
					try {
						if (usuariosPadron.isEmpty())
							Contexto.getInstance().getManagerBeanRemote().encolarEnvioMasivo(usuariosAuditor, template);
						else
							Contexto.getInstance().getManagerBeanRemote().encolarEnvioMasivo(usuariosPadron, template);
						getSession().info(getString("prevDestExito"));
						setResponsePage(DashboardMensajes.class, UtilsParameters.getId(template.getEleccion().getIdEleccion()));
					} catch (Exception e) {
						appLogger.error(e);
					}

				}
			});

			add(new BookmarkablePageLink<>("cancelar", DashboardPlantillasVer.class, UtilsParameters.getId(template.getEleccion().getIdEleccion())));

		} catch (Exception e) {
			appLogger.error(e);
		}
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
}
