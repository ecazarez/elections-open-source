package net.lacnic.siselecciones.admin.dashboard.user;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.lacnic.siselecciones.admin.app.Contexto;
import net.lacnic.siselecciones.admin.dashboard.error.Error404;
import net.lacnic.siselecciones.admin.dashboard.error.ErrorResultadosNoPublicos;
import net.lacnic.siselecciones.admin.web.bases.DashboardPublicBasePage;
import net.lacnic.siselecciones.admin.web.panel.elecciones.CodigosCandidatoPanel;
import net.lacnic.siselecciones.admin.web.panel.elecciones.ResultadoEleccionPanel;
import net.lacnic.siselecciones.dominio.Eleccion;

public class DashboardResultado extends DashboardPublicBasePage {

	private static final long serialVersionUID = 2304496268074384354L;

	private static final Logger appLogger = LogManager.getLogger("webAdminAppLogger");

	public DashboardResultado(PageParameters params) {
		super(params);
		try {
			add(new Label("titulo", getEleccion().getTitulo(getIdioma())));
			Label desc = new Label("descripcion", getEleccion().getDescripcion(getIdioma()));
			desc.setEscapeModelStrings(false);
			add(desc);
			add(new ResultadoEleccionPanel("resultadoPanel", getEleccion().getIdEleccion()));
			add(new CodigosCandidatoPanel("codigosCandidatoPanel", getEleccion().getIdEleccion()));

		} catch (Exception e) {
			appLogger.error(e);
		}
	}

	@Override
	public Class validarToken(PageParameters params) {
		Eleccion eleccion = Contexto.getInstance().getVotanteBeanRemote().verificarAccesoResultado(getToken());
		if (eleccion == null) {
			Contexto.getInstance().getVotanteBeanRemote().intentoFallidoIp(getIP());
			return Error404.class;
		} else {
			setEleccion(eleccion);
			if (!eleccion.isHabilitadoLinkResultado()) {
				return ErrorResultadosNoPublicos.class;
			}
		}
		return null;
	}
}