/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.report.core;

import java.util.List;

import org.specrunner.SRServices;

/**
 * Dumper of result resumes.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResumeDumper {

    /**
     * List of parts.
     * 
     * @return List of reporter parts.
     */
    List<ReportPart> getParts();

    /**
     * Partial resume.
     * 
     * @param services
     *            The services instance.
     * @param parent
     *            The parent reporter.
     * @param finalResume
     *            If it is the final resume.
     * @return The resume.
     */
    Object resume(SRServices services, ResumeReporter parent, boolean finalResume);

    /**
     * Dump report starting.
     * 
     * @param services
     *            Current instance.
     * @param parent
     *            The parent reporter.
     */
    void dumpStart(SRServices services, ResumeReporter parent);

    /**
     * Dump the resume declaration.
     * 
     * @param services
     *            Current instance.
     * @param parent
     *            The parent reporter.
     * @param header
     *            The header.
     * @param list
     *            The list of resumes.
     */
    void dumpPart(SRServices services, ResumeReporter parent, String header, List<Resume> list);

    /**
     * Dump resume.
     * 
     * @param services
     *            Current instance.
     * @param parent
     *            The parent reporter.
     * @param resume
     *            Resume information.
     */
    void dumpResume(SRServices services, ResumeReporter parent, Object resume);

    /**
     * Dump report ending.
     * 
     * @param services
     *            Current instance.
     * @param parent
     *            The parent reporter.
     */
    void dumpEnd(SRServices services, ResumeReporter parent);

}
